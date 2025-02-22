<template>
  <!-- TODO:接口请求超过5S以上可展示取消请求按钮，避免用户过长等待 -->
  <MsCard :loading="loading" show-full-screen simple no-content-padding>
    <MsSplitBox :size="300" :max="0.5">
      <template #first>
        <moduleTree
          ref="moduleTreeRef"
          :active-node-id="activeDebug.id"
          @init="(val) => (folderTree = val)"
          @new-api="addDebugTab"
          @click-api-node="openApiTab"
          @import="importDrawerVisible = true"
          @update-api-node="handleApiUpdateFromModuleTree"
          @delete-finish="handleDeleteFinish"
        />
      </template>
      <template #second>
        <div class="flex h-full flex-col">
          <div class="border-b border-[var(--color-text-n8)] p-[12px_18px]">
            <MsEditableTab
              v-model:active-tab="activeDebug"
              v-model:tabs="debugTabs"
              :readonly="!hasAnyPermission(['PROJECT_API_DEBUG:READ+ADD'])"
              at-least-one
              @add="addDebugTab"
            >
              <template #label="{ tab }">
                <apiMethodName :method="tab.protocol === 'HTTP' ? tab.method : tab.protocol" class="mr-[4px]" />
                <a-tooltip :content="tab.name || tab.label" :mouse-enter-delay="500">
                  <div class="one-line-text max-w-[144px]">
                    {{ tab.name || tab.label }}
                  </div>
                </a-tooltip>
              </template>
            </MsEditableTab>
          </div>
          <div class="flex-1 overflow-hidden">
            <debug
              v-model:detail-loading="loading"
              v-model:request="activeDebug"
              :module-tree="folderTree"
              :create-api="addDebug"
              :update-api="updateDebug"
              :execute-api="executeDebug"
              :local-execute-api="localExecuteApiDebug"
              :upload-temp-file-api="uploadTempFile"
              :file-save-as-source-id="activeDebug.id"
              :file-save-as-api="transferFile"
              :file-module-options-api="getTransferOptions"
              :permission-map="{
                execute: 'PROJECT_API_DEBUG:READ+EXECUTE',
                update: 'PROJECT_API_DEBUG:READ+UPDATE',
                create: 'PROJECT_API_DEBUG:READ+ADD',
              }"
              @add-done="handleDebugAddDone"
            />
          </div>
        </div>
      </template>
    </MsSplitBox>
  </MsCard>
  <MsDrawer
    v-model:visible="importDrawerVisible"
    :width="680"
    :ok-disabled="curlCode.trim() === ''"
    disabled-width-drag
    @cancel="curlCode = ''"
    @confirm="handleCurlImportConfirm"
  >
    <template #title>
      <a-tooltip position="right" :content="t('apiTestDebug.importByCURLTip')">
        <span
          >{{ t('apiTestDebug.importByCURL') }}
          <icon-exclamation-circle
            class="ml-[4px] text-[var(--color-text-brand)] hover:text-[rgb(var(--primary-5))]"
            size="16"
          />
        </span>
      </a-tooltip>
    </template>
    <div class="h-full">
      <MsCodeEditor
        v-if="importDrawerVisible"
        v-model:model-value="curlCode"
        theme="MS-text"
        height="100%"
        :language="LanguageEnum.PLAINTEXT"
        :show-theme-change="false"
        :show-full-screen="false"
      >
      </MsCodeEditor>
    </div>
  </MsDrawer>
</template>

<script lang="ts" setup>
  /**
   * @description 接口测试-接口调试
   */
  import { useRoute } from 'vue-router';
  import { cloneDeep } from 'lodash-es';

  import MsCard from '@/components/pure/ms-card/index.vue';
  import MsCodeEditor from '@/components/pure/ms-code-editor/index.vue';
  import { LanguageEnum } from '@/components/pure/ms-code-editor/types';
  import MsDrawer from '@/components/pure/ms-drawer/index.vue';
  import MsEditableTab from '@/components/pure/ms-editable-tab/index.vue';
  import { TabItem } from '@/components/pure/ms-editable-tab/types';
  import MsSplitBox from '@/components/pure/ms-split-box/index.vue';
  import moduleTree from './components/moduleTree.vue';
  import apiMethodName from '@/views/api-test/components/apiMethodName.vue';
  import debug, { RequestParam } from '@/views/api-test/components/requestComposition/index.vue';

  import { localExecuteApiDebug } from '@/api/modules/api-test/common';
  import {
    addDebug,
    executeDebug,
    getDebugDetail,
    getTransferOptions,
    transferFile,
    updateDebug,
    uploadTempFile,
  } from '@/api/modules/api-test/debug';
  import { useI18n } from '@/hooks/useI18n';
  import useLeaveTabUnSaveCheck from '@/hooks/useLeaveTabUnSaveCheck';
  import { parseCurlScript } from '@/utils';
  import { hasAnyPermission } from '@/utils/permission';

  import { ModuleTreeNode } from '@/models/common';
  import {
    RequestAuthType,
    RequestComposition,
    RequestContentTypeEnum,
    RequestMethods,
    RequestParamsType,
    ResponseComposition,
  } from '@/enums/apiEnum';

  import { defaultBodyParams, defaultResponse } from '../components/config';
  import { parseRequestBodyFiles } from '../components/utils';

  const route = useRoute();
  const { t } = useI18n();

  const moduleTreeRef = ref<InstanceType<typeof moduleTree>>();
  const folderTree = ref<ModuleTreeNode[]>([]);
  const importDrawerVisible = ref(false);
  const curlCode = ref('');
  const loading = ref(false);

  async function handleDebugAddDone() {
    await moduleTreeRef.value?.initModules();
    moduleTreeRef.value?.initModuleCount();
  }

  const initDefaultId = `debug-${Date.now()}`;
  const defaultDebugParams: RequestParam = {
    type: 'api',
    id: initDefaultId,
    moduleId: 'root',
    protocol: 'HTTP',
    url: '',
    activeTab: RequestComposition.HEADER,
    label: t('apiTestDebug.newApi'),
    closable: true,
    method: RequestMethods.GET,
    unSaved: false,
    headers: [],
    body: cloneDeep(defaultBodyParams),
    query: [],
    rest: [],
    polymorphicName: '',
    name: '',
    path: '',
    projectId: '',
    uploadFileIds: [],
    linkFileIds: [],
    authConfig: {
      authType: RequestAuthType.NONE,
      basicAuth: {
        userName: '',
        password: '',
      },
      digestAuth: {
        userName: '',
        password: '',
      },
    },
    children: [
      {
        polymorphicName: 'MsCommonElement', // 协议多态名称，写死MsCommonElement
        assertionConfig: {
          enableGlobal: false,
          assertions: [],
        },
        postProcessorConfig: {
          enableGlobal: false,
          processors: [],
        },
        preProcessorConfig: {
          enableGlobal: false,
          processors: [],
        },
      },
    ],
    otherConfig: {
      connectTimeout: 60000,
      responseTimeout: 60000,
      certificateAlias: '',
      followRedirects: true,
      autoRedirects: false,
    },
    responseActiveTab: ResponseComposition.BODY,
    response: cloneDeep(defaultResponse),
    isNew: true,
    executeLoading: false,
    errorMessageInfo: {},
  };
  const debugTabs = ref<RequestParam[]>([cloneDeep(defaultDebugParams)]);
  const activeDebug = ref<RequestParam>(debugTabs.value[0]);

  function handleActiveDebugChange() {
    if (!loading.value) {
      // 如果是因为加载详情触发的change则不需要标记为未保存
      activeDebug.value.unSaved = true;
    }
  }

  function addDebugTab(defaultProps?: Partial<TabItem>) {
    const id = `debug-${Date.now()}`;
    debugTabs.value.push({
      ...cloneDeep(defaultDebugParams),
      id,
      isNew: !defaultProps?.id, // 新开的tab标记为前端新增的调试，因为此时都已经有id了；但是如果是查看打开的会有携带id
      ...defaultProps,
    });
    activeDebug.value = debugTabs.value[debugTabs.value.length - 1];
  }

  async function openApiTab(apiInfo: ModuleTreeNode | string) {
    const id = typeof apiInfo === 'string' ? apiInfo : apiInfo.id;
    const isLoadedTabIndex = debugTabs.value.findIndex((e) => e.id === id);
    if (isLoadedTabIndex > -1) {
      // 如果点击的请求在tab中已经存在，则直接切换到该tab
      activeDebug.value = debugTabs.value[isLoadedTabIndex];
      return;
    }
    try {
      loading.value = true;
      const res = await getDebugDetail(id);
      let parseRequestBodyResult;
      if (res.protocol === 'HTTP') {
        parseRequestBodyResult = parseRequestBodyFiles(res.request.body); // 解析请求体中的文件，将详情中的文件 id 集合收集，更新时以判断文件是否删除以及是否新上传的文件
      }
      addDebugTab({
        ...res,
        response: cloneDeep(defaultResponse),
        ...res.request,
        url: res.path,
        label: res.name,
        name: res.name, // request里面还有个name但是是null
        moduleId: res.moduleId, // request里面还有个moduleId但是是null
        ...parseRequestBodyResult,
      });
      nextTick(() => {
        // 等待内容渲染出来再隐藏loading
        loading.value = false;
      });
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      loading.value = false;
    }
  }

  function handleCurlImportConfirm() {
    const { url, headers, queryParameters } = parseCurlScript(curlCode.value);
    addDebugTab({
      url,
      headers: headers?.map((e) => ({
        contentType: RequestContentTypeEnum.TEXT,
        description: '',
        enable: true,
        ...e,
      })),
      query: queryParameters?.map((e) => ({
        paramType: RequestParamsType.STRING,
        description: '',
        required: false,
        maxLength: undefined,
        minLength: undefined,
        encode: false,
        enable: true,
        ...e,
      })),
    });
    curlCode.value = '';
    importDrawerVisible.value = false;
    nextTick(() => {
      handleActiveDebugChange();
    });
  }

  /**
   * 同步模块树的接口信息更新操作
   */
  function handleApiUpdateFromModuleTree(newInfo: { id: string; name: string; moduleId?: string; [key: string]: any }) {
    debugTabs.value = debugTabs.value.map((item) => {
      if (item.id === newInfo.id) {
        item.label = newInfo.name;
        item.name = newInfo.name;
        if (newInfo.moduleId) {
          item.moduleId = newInfo.moduleId;
        }
      }
      return item;
    });
    if (activeDebug.value.id === newInfo.id) {
      activeDebug.value.label = newInfo.name;
      activeDebug.value.name = newInfo.name;
      if (newInfo.moduleId) {
        activeDebug.value.moduleId = newInfo.moduleId;
      }
    }
  }

  /**
   * 同步模块树的接口信息删除操作
   * @param id 接口 id
   * @param isModule 是否是删除模块
   */
  function handleDeleteFinish(node: ModuleTreeNode) {
    if (node.type === 'MODULE') {
      // 删除整个模块
      debugTabs.value = debugTabs.value.filter((item) => {
        if (activeDebug.value.id === item.id) {
          // 删除的是当前激活的 tab, 切换到第一个 tab
          [activeDebug.value] = debugTabs.value;
        }
        return item.moduleId !== node.id || item.id === 'all';
      });
    } else {
      // 删除单个 api
      debugTabs.value = debugTabs.value.filter((item) => item.id !== node.id);
      if (activeDebug.value.id === node.id) {
        [activeDebug.value] = debugTabs.value;
      }
    }
  }

  onMounted(() => {
    if (route.query.id) {
      openApiTab(route.query.id as string);
    }
  });

  useLeaveTabUnSaveCheck(debugTabs.value, ['PROJECT_API_DEBUG:READ+ADD', 'PROJECT_API_DEBUG:READ+UPDATE']);
</script>

<style lang="less" scoped></style>
