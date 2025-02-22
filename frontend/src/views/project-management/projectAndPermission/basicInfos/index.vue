<template>
  <div v-if="projectDetail?.deleted" class="mb-6">
    <a-alert type="error">{{ t('project.basicInfo.alertDescription') }}</a-alert>
  </div>
  <div class="wrapper mb-6 flex justify-between">
    <span class="font-medium text-[var(--color-text-000)]">{{ t('project.basicInfo.basicInfo') }}</span>
    <a-button
      v-if="!projectDetail?.deleted"
      v-permission="['PROJECT_BASE_INFO:READ+UPDATE']"
      type="outline"
      @click="editHandler"
      >{{ t('project.basicInfo.edit') }}</a-button
    >
  </div>
  <div class="project-info mb-6 h-[112px] bg-white p-1">
    <div class="inner-wrapper rounded-md p-4">
      <div class="detail-info flex flex-col justify-between rounded-md p-4">
        <div class="flex items-center">
          <span class="one-line-text mr-1 max-w-[300px] font-medium text-[var(--color-text-000)]">{{
            projectDetail?.name
          }}</span>
          <span
            v-if="!projectDetail?.deleted && projectDetail?.enable"
            class="button mr-1"
            :class="[projectDetail?.enable ? 'enable-button' : 'delete-button']"
            >{{ projectDetail?.enable ? t('project.basicInfo.enable') : t('project.basicInfo.enable') }}</span
          >
        </div>
        <div class="one-line-text text-xs text-[--color-text-4]">{{ projectDetail?.description }}</div>
      </div>
    </div>
  </div>
  <div class="ml-1 flex flex-col">
    <div class="label-item">
      <span class="label">{{ t('project.basicInfo.createBy') }}</span>
      <a-tooltip v-if="translateTextToPX(projectDetail?.createUser) > 300" :content="projectDetail?.createUser">
        <span class="one-line-text" style="max-width: 300px">{{ projectDetail?.createUser }}</span>
      </a-tooltip>
      <span v-else>{{ projectDetail?.createUser }}</span>
    </div>
    <div class="label-item">
      <span class="label">{{ t('project.basicInfo.organization') }}</span>
      <MsTag>{{ projectDetail?.organizationName }}</MsTag>
    </div>
    <div class="label-item">
      <span class="label">{{ t('project.basicInfo.resourcePool') }}</span>
      <MsTag v-for="pool of projectDetail?.resourcePoolList" :key="pool.id">{{ pool.name }}</MsTag>
    </div>
    <div class="label-item">
      <span class="label">{{ t('project.basicInfo.createTime') }}</span>
      <span>{{ dayjs(projectDetail?.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
    </div>
  </div>
  <UpdateProjectModal ref="projectDetailRef" v-model:visible="isVisible" @success="getProjectDetail()" />
</template>

<script setup lang="ts">
  /**
   * @description 项目管理-项目与权限-基本信息
   */
  import { inject, onBeforeMount, ref } from 'vue';
  import dayjs from 'dayjs';

  import MsTag from '@/components/pure/ms-tag/ms-tag.vue';
  import UpdateProjectModal from './components/updateProjectModal.vue';

  import { getProjectInfo } from '@/api/modules/project-management/basicInfo';
  import { useI18n } from '@/hooks/useI18n';
  import { useAppStore } from '@/store';
  import { translateTextToPX } from '@/utils/css';

  import type { ProjectBasicInfoModel } from '@/models/projectManagement/basicInfo';

  const { t } = useI18n();
  const appStore = useAppStore();

  const currentProjectId = computed(() => appStore.currentProjectId);

  const updateLoading = inject('reload', (flag: boolean) => {});

  const projectDetail = ref<ProjectBasicInfoModel>();

  const getProjectDetail = async () => {
    updateLoading(true);
    try {
      projectDetail.value = await getProjectInfo(currentProjectId.value);
    } catch (error) {
      console.log(error);
    } finally {
      updateLoading(false);
    }
  };

  const isVisible = ref<boolean>(false);
  const projectDetailRef = ref();

  const editHandler = () => {
    isVisible.value = true;
    projectDetailRef.value.editProject(projectDetail.value);
  };

  onBeforeMount(async () => {
    getProjectDetail();
  });
</script>

<style scoped lang="less">
  .project-info {
    border-radius: 4px;
    box-shadow: 0 0 10px rgb(120 56 135/5%);
    .inner-wrapper {
      height: 100%;
      background: rgb(var(--primary-1));
      .detail-info {
        height: 100%;
        background: url('@/assets/images/basic_bg.png');
        background-size: cover;
        .button {
          border-radius: 2px;
          @apply inline-block px-2 py-1 text-xs;
        }
        .enable-button {
          color: rgb(var(--success-5));
          background: rgb(var(--success-1));
        }
        .disabled-button {
          color: var(--color-text-4);
          background: var(--color-text-n8);
        }
        .delete-button {
          color: rgb(var(--danger-5));
          background: rgb(var(--danger-1));
        }
      }
    }
  }
  .label-item {
    margin-bottom: 16px;
    height: 22px;
    line-height: 22px;
    span {
      float: left;
    }
    .label {
      margin-right: 16px;
      width: 120px;
      color: var(--color-text-3);
    }
  }
</style>
