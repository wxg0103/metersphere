<template>
  <!-- 所属平台一致, 详情展示 -->
  <div v-if="props.currentPlatform === props.detailInfo.platform" class="relative p-[16px] pb-[16px]">
    <div class="header">
      <div v-permission="['PROJECT_BUG:READ+UPDATE']" class="header-action">
        <a-button type="text" @click="contentEditAble = !contentEditAble">
          <template #icon> <MsIconfont type="icon-icon_edit_outlined" /> </template>
          {{ t('bugManagement.edit.contentEdit') }}
        </a-button>
      </div>
    </div>
    <a-form ref="caseFormRef" class="rounded-[4px]" :model="form" layout="vertical">
      <!-- 左侧布局默认内容(非平台默认模板时默认展示) -->
      <div v-if="!isPlatformDefaultTemplate" class="default-content !break-words break-all">
        <div class="header-title">
          <strong>
            {{ t('bugManagement.edit.content') }}
          </strong>
        </div>
        <div class="mb-4 mt-[16px]" :class="{ 'max-h-[260px]': contentEditAble }">
          <MsRichText
            v-if="contentEditAble"
            v-model:raw="form.description"
            v-model:filed-ids="descriptionFileIds"
            :disabled="!contentEditAble"
            :placeholder="t('editor.placeholder')"
            :upload-image="handleUploadImage"
            :preview-url="EditorPreviewFileUrl"
          />
          <div v-else v-dompurify-html="form?.description || '-'" class="markdown-body"></div>
        </div>
        <div v-if="contentEditAble" class="mt-[8px] flex justify-end">
          <a-button type="secondary" @click="handleCancel">{{ t('common.cancel') }}</a-button>
          <a-button class="ml-[12px]" type="primary" :loading="confirmLoading" @click="handleSave">
            {{ t('common.save') }}
          </a-button>
        </div>
      </div>
      <!-- 特殊布局内容(平台默认模板时展示) -->
      <div v-if="isPlatformDefaultTemplate" class="special-content">
        <div v-for="(item, index) in platformSystemFields" :key="index">
          <div v-if="item.fieldId !== 'summary'">
            <h1 class="header-title">
              <strong>{{ item.fieldName }}</strong>
            </h1>
            <div class="mb-4 mt-[16px]" :class="{ 'max-h-[260px]': contentEditAble }">
              <MsRichText
                v-if="contentEditAble"
                v-model:raw="item.defaultValue"
                v-model:filed-ids="descriptionFileIdMap[item.fieldId]"
                :disabled="!contentEditAble"
                :placeholder="t('editor.placeholder')"
                :upload-image="handleUploadImage"
                :preview-url="EditorPreviewFileUrl"
              />
              <div v-else v-dompurify-html="item?.defaultValue || '-'" class="markdown-body"></div>
            </div>
          </div>
        </div>
        <div v-if="contentEditAble" class="mt-[8px] flex justify-end">
          <a-button type="secondary" @click="handleCancel">{{ t('common.cancel') }}</a-button>
          <a-button class="ml-[12px]" type="primary" :loading="confirmLoading" @click="handleSave">
            {{ t('common.save') }}
          </a-button>
        </div>
      </div>
      <!-- 附件布局 -->
      <div class="mt-6">
        <AddAttachment
          v-model:file-list="fileList"
          :disabled="!hasAnyPermission(['PROJECT_BUG:READ+UPDATE'])"
          @link-file="associatedFile"
        />
      </div>
    </a-form>
    <MsFileList
      ref="fileListRef"
      v-model:file-list="fileList"
      :show-tab="false"
      :request-params="{
        bugId: bugId,
        projectId: currentProjectId,
      }"
      :upload-func="uploadOrAssociationFile"
      :handle-delete="deleteFileHandler"
      :show-delete="props.allowEdit"
      :init-file-save-tips="t('ms.upload.waiting_save')"
      @finish="uploadFileOver"
    >
      <template #actions="{ item }">
        <div>
          <!-- 本地文件 -->
          <div v-if="item.local || item.status === 'init'" class="flex flex-nowrap">
            <MsButton
              v-if="item.status !== 'init' && item.status !== 'uploading' && item.file.type.includes('image')"
              type="button"
              status="primary"
              class="!mr-[4px]"
              @click="handlePreview(item)"
            >
              {{ t('ms.upload.preview') }}
            </MsButton>
            <MsButton
              v-if="item.status === 'done' && hasAnyPermission(['PROJECT_BUG:READ+UPDATE'])"
              type="button"
              status="primary"
              class="!mr-[4px]"
              @click="transferVisible = true"
            >
              {{ t('caseManagement.featureCase.storage') }}
            </MsButton>
            <TransferModal
              v-model:visible="transferVisible"
              :request-fun="transferFileRequest"
              :params="{
                projectId: currentProjectId,
                bugId: bugId,
                fileId: item.uid,
                local: true,
              }"
              @success="emit('updateSuccess')"
            />
            <MsButton
              v-if="item.status === 'done'"
              type="button"
              status="primary"
              class="!mr-[4px]"
              @click="downloadFile(item)"
            >
              {{ t('caseManagement.featureCase.download') }}
            </MsButton>
          </div>
          <!-- 关联文件 -->
          <div v-else class="flex flex-nowrap">
            <MsButton
              v-if="item.status !== 'init' && item.file.type.includes('image')"
              type="button"
              status="primary"
              class="!mr-[4px]"
              @click="handlePreview(item)"
            >
              {{ t('ms.upload.preview') }}
            </MsButton>
            <MsButton
              v-if="item.status === 'done'"
              type="button"
              status="primary"
              class="!mr-[4px]"
              @click="downloadFile(item)"
            >
              {{ t('caseManagement.featureCase.download') }}
            </MsButton>
            <MsButton
              v-if="item.isUpdateFlag && hasAnyPermission(['PROJECT_BUG:READ+UPDATE'])"
              type="button"
              status="primary"
              class="!mr-[4px]"
              @click="handleUpdateFile(item)"
            >
              {{ t('common.update') }}
            </MsButton>
          </div>
        </div>
      </template>
      <template #title="{ item }">
        <span v-if="item.isUpdateFlag" class="ml-4 flex items-center font-normal text-[rgb(var(--warning-6))]"
          ><icon-exclamation-circle-fill /> <span>{{ t('caseManagement.featureCase.fileIsUpdated') }}</span>
        </span>
      </template>
    </MsFileList>
  </div>

  <!-- 所属平台不一致, 详情不展示, 展示空面板 -->
  <div v-else class="empty-panel">
    <a-empty> {{ t('bugManagement.detail.platform_no_active') }} </a-empty>
  </div>
  <div>
    <MsUpload
      v-model:file-list="fileList"
      accept="none"
      :auto-upload="false"
      :sub-text="acceptType === 'jar' ? '' : t('project.fileManagement.normalFileSubText', { size: 50 })"
      multiple
      draggable
      size-unit="MB"
      :max-size="50"
      :is-all-screen="true"
      class="mb-[16px]"
    />
  </div>
  <RelateFileDrawer
    v-model:visible="associatedDrawer"
    :get-tree-request="getModules"
    :get-count-request="getModulesCount"
    :get-list-request="getAssociatedFileList"
    :get-list-fun-params="getListFunParams"
    @save="saveSelectAssociatedFile"
  />
  <a-image-preview v-model:visible="previewVisible" :src="imageUrl" />
</template>

<script setup lang="ts">
  import { Message } from '@arco-design/web-vue';

  import MsButton from '@/components/pure/ms-button/index.vue';
  import { FormRuleItem } from '@/components/pure/ms-form-create/types';
  import MsIconfont from '@/components/pure/ms-icon-font/index.vue';
  import MsRichText from '@/components/pure/ms-rich-text/MsRichText.vue';
  import MsFileList from '@/components/pure/ms-upload/fileList.vue';
  import MsUpload from '@/components/pure/ms-upload/index.vue';
  import { MsFileItem } from '@/components/pure/ms-upload/types';
  import AddAttachment from '@/components/business/ms-add-attachment/index.vue';
  import RelateFileDrawer from '@/components/business/ms-link-file/associatedFileDrawer.vue';
  import TransferModal from '@/views/case-management/caseManagementFeature/components/tabContent/transferModal.vue';

  import {
    checkFileIsUpdateRequest,
    createOrUpdateBug,
    deleteFileOrCancelAssociation,
    downloadFileRequest,
    editorUploadFile,
    getAssociatedFileList,
    getAttachmentList,
    previewFile,
    transferFileRequest,
    updateFile,
    uploadOrAssociationFile,
  } from '@/api/modules/bug-management';
  import { getModules, getModulesCount } from '@/api/modules/project-management/fileManagement';
  import { EditorPreviewFileUrl } from '@/api/requrls/bug-management';
  import { useI18n } from '@/hooks/useI18n';
  import { useAppStore } from '@/store';
  import { downloadByteFile, sleep } from '@/utils';
  import { hasAnyPermission } from '@/utils/permission';
  import { findParents, Option } from '@/utils/recursion';

  import { BugEditCustomField, BugEditCustomFieldItem, BugEditFormObject } from '@/models/bug-management';
  import { AssociatedList, AttachFileInfo } from '@/models/caseManagement/featureCase';
  import { TableQueryParams } from '@/models/common';

  import { convertToFileByBug, convertToFileByDetail } from '@/views/bug-management/utils';

  defineOptions({
    name: 'BugDetailTab',
  });

  const { t } = useI18n();

  const props = defineProps<{
    detailInfo: BugEditFormObject;
    formItem: FormRuleItem[];
    allowEdit?: boolean; // 是否允许编辑
    isPlatformDefaultTemplate: boolean; // 是否是平台默认模板
    platformSystemFields: BugEditCustomField[]; // 平台系统字段
    currentPlatform: string; // 当前平台
  }>();

  const emit = defineEmits<{
    (e: 'updateSuccess'): void;
  }>();

  const appStore = useAppStore();
  const transferVisible = ref<boolean>(false);
  const previewVisible = ref<boolean>(false);
  const acceptType = ref('none'); // 模块-上传文件类型
  // 描述-富文本临时附件ID
  const descriptionFileIds = ref<string[]>([]);
  const descriptionFileIdMap = ref<Record<string, string[]>>({});
  const imageUrl = ref<string>('');
  const associatedDrawer = ref(false);
  const fileListRef = ref<InstanceType<typeof MsFileList>>();
  // 富文本编辑器是否可编辑
  const contentEditAble = ref(false);
  // 富文本框默认值。用于取消编辑时的复位
  const defaultContentValue = ref<string>('');
  const currentProjectId = computed(() => appStore.currentProjectId);
  // 前端保存的fileList
  const fileList = ref<MsFileItem[]>([]);
  const bugId = computed(() => props.detailInfo.id);
  // 后端保存的文件list
  const attachmentsList = ref<AttachFileInfo[]>([]);
  const getListFunParams = ref<TableQueryParams>({
    combine: {
      hiddenIds: [],
    },
  });
  const form = ref({
    title: '',
    description: '',
    deleteLocalFileIds: [] as string[],
    unLinkRefIds: [] as string[],
    linkFileIds: [] as string[],
  });

  const handleFileFunc = async (attachments: AttachFileInfo[]) => {
    if (attachments && attachments.length) {
      attachmentsList.value = attachments;
      // 检查文件是否有更新
      const checkUpdateFileIds = await checkFileIsUpdateRequest(attachments.map((item: any) => item.fileId));
      // 处理文件列表
      fileList.value =
        attachments
          .map((fileInfo: any) => {
            return {
              ...fileInfo,
              name: fileInfo.fileName,
              isUpdateFlag: checkUpdateFileIds.includes(fileInfo.fileId),
              showDelete: hasAnyPermission(['PROJECT_BUG:READ+UPDATE']),
            };
          })
          .map((fileInfo: any) => {
            return convertToFileByBug(fileInfo);
          }) || [];
    } else {
      fileList.value = [];
    }
  };

  const confirmLoading = ref<boolean>(false);

  const initCurrentDetail = async (detail: BugEditFormObject) => {
    const { attachments, title, description } = detail;
    form.value.title = title;
    form.value.description = description;
    defaultContentValue.value = description;
    await handleFileFunc(attachments);
  };

  // 取消富文本框编辑时，要将数据复位
  function handleCancel() {
    contentEditAble.value = false;
    form.value.description = defaultContentValue.value;
  }

  // 删除本地文件
  async function deleteFileHandler(item: MsFileItem) {
    try {
      const params = {
        refId: item.associateId,
        associated: !item.local,
        bugId: bugId.value,
        projectId: currentProjectId.value,
      };
      await deleteFileOrCancelAssociation(params);
      Message.success(
        item.local ? t('caseManagement.featureCase.deleteSuccess') : t('caseManagement.featureCase.cancelLinkSuccess')
      );
      const attachments = await getAttachmentList(bugId.value);
      await handleFileFunc(attachments);
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    }
  }

  function beforeUpload(file: File) {
    const _maxSize = 50 * 1024 * 1024;
    if (file.size > _maxSize) {
      Message.warning(t('ms.upload.overSize'));
      return Promise.resolve(false);
    }
    return Promise.resolve(true);
  }

  // 预览图片
  async function handlePreview(item: MsFileItem) {
    try {
      imageUrl.value = '';
      previewVisible.value = true;
      if (item.status !== 'init') {
        const res = await previewFile({
          projectId: currentProjectId.value,
          bugId: bugId.value as string,
          fileId: item.uid,
          associated: !item.local,
        });
        const blob = new Blob([res], { type: 'image/jpeg' });
        imageUrl.value = URL.createObjectURL(blob);
      } else {
        imageUrl.value = item.url || '';
      }
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    }
  }

  // 下载
  async function downloadFile(item: MsFileItem) {
    try {
      const res = await downloadFileRequest({
        projectId: currentProjectId.value,
        bugId: bugId.value as string,
        fileId: item.uid,
        associated: !item.local,
      });
      downloadByteFile(res, `${item.name}`);
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    }
  }

  function associatedFile() {
    associatedDrawer.value = true;
  }

  async function startUpload() {
    await sleep(300);
    fileListRef.value?.startUpload();
  }

  async function uploadFileOver() {
    emit('updateSuccess');
  }

  // 文件列表单个上传
  watch(
    () => fileList.value,
    async (val) => {
      const isNewFiles = val.filter((item) => item.status === 'init').length;
      if (val && isNewFiles) {
        await startUpload();
      }
      getListFunParams.value.combine.hiddenIds = fileList.value.filter((item) => !item.local).map((item) => item.uid);
    }
  );

  // 更新文件
  async function handleUpdateFile(item: MsFileItem) {
    try {
      const params = {
        refId: item.associateId,
        associated: !item.local,
        bugId: bugId.value,
        projectId: currentProjectId.value,
      };
      await updateFile(params);
      Message.success(t('common.updateSuccess'));
      const attachments = await getAttachmentList(bugId.value);
      await handleFileFunc(attachments);
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    }
  }

  async function handleUploadImage(file: File) {
    const { data } = await editorUploadFile({
      fileList: [file],
    });
    return data;
  }

  function getDescriptionFileId() {
    const fileIds = [] as string[];
    Object.keys(descriptionFileIdMap.value).forEach((key) => {
      if (descriptionFileIdMap.value[key].length > 0) {
        fileIds.push(...descriptionFileIdMap.value[key]);
      }
    });
    return fileIds;
  }

  // 保存操作
  async function handleSave() {
    try {
      confirmLoading.value = true;
      const { formItem } = props;
      const customFields: BugEditCustomFieldItem[] = [];
      if (formItem && formItem.length) {
        formItem.forEach((item: FormRuleItem) => {
          let itemVal = item.value;
          if (item.sourceType === 'CASCADER') {
            itemVal = findParents(item.options as Option[], item.value as string, []);
          }
          customFields.push({
            id: item.field as string,
            name: item.title as string,
            type: item.sourceType as string,
            value: Array.isArray(itemVal) ? JSON.stringify(itemVal) : (itemVal as string),
          });
        });
      }
      if (props.isPlatformDefaultTemplate) {
        // 平台系统默认字段插入自定义集合
        props.platformSystemFields.forEach((item) => {
          customFields.push({
            id: item.fieldId,
            name: item.fieldName,
            type: item.type,
            value: item.defaultValue,
          });
        });
      }
      const tmpObj: BugEditFormObject = {
        id: props.detailInfo.id,
        projectId: currentProjectId.value,
        templateId: props.detailInfo.templateId,
        tags: props.detailInfo.tags,
        deleteLocalFileIds: form.value.deleteLocalFileIds,
        unLinkRefIds: form.value.unLinkRefIds,
        linkFileIds: form.value.linkFileIds,
        customFields,
        richTextTmpFileIds: props.isPlatformDefaultTemplate ? getDescriptionFileId() : descriptionFileIds.value,
      };
      if (!props.isPlatformDefaultTemplate) {
        tmpObj.description = form.value.description;
        tmpObj.title = form.value.title;
      }
      // 执行保存操作。 保存成功后将富文本内容赋值给默认值
      const res = await createOrUpdateBug({ request: tmpObj, fileList: [] as unknown as File[] });
      if (res) {
        Message.success(t('common.updateSuccess'));
        defaultContentValue.value = form.value.description;
        handleCancel();
        emit('updateSuccess');
      }
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    } finally {
      confirmLoading.value = false;
    }
  }

  // 关联文件
  async function saveSelectAssociatedFile(fileData: AssociatedList[]) {
    const fileResultList = fileData.map(convertToFileByDetail);
    fileList.value.push(...fileResultList);
    const params = {
      request: {
        bugId: bugId.value as string,
        projectId: currentProjectId.value,
        selectIds: fileResultList.map((item: any) => item.uid),
      },
    };
    await uploadOrAssociationFile(params);
    const attachments = await getAttachmentList(bugId.value);
    await handleFileFunc(attachments);
    Message.success(t('common.linkSuccess'));
  }

  watchEffect(() => {
    initCurrentDetail(props.detailInfo);
  });
  defineExpose({
    handleSave,
  });
</script>

<style lang="less" scoped>
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    &-title {
      font-weight: 500;
      color: var(--color-text-1);
    }
    &-action {
      position: absolute;
      top: 0;
      right: 6px;
      color: rgb(var(--primary-7));
    }
  }
</style>

<style scoped lang="less">
  :deep(.arco-form-item-label) {
    font-weight: bold !important;
  }
</style>