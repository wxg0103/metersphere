<template>
  <div class="history-container">
    <a-alert v-if="!getIsVisited()" :show-icon="false" class="mb-[16px]" type="warning" closable @close="addVisited">
      {{ t('apiTestManagement.historyListTip') }}
      <template #close-element>
        <span class="text-[14px]">{{ t('common.notRemind') }}</span>
      </template>
    </a-alert>
    <ms-base-table v-bind="propsRes" no-disable v-on="propsEvent">
      <!-- <template #action="{ record }">
        <div class="flex items-center">
          <MsButton type="text" @click="recover(record)">{{ t('apiTestManagement.recover') }}</MsButton>
        </div>
      </template> -->
    </ms-base-table>
  </div>
</template>

<script setup lang="ts">
  import dayjs from 'dayjs';

  // import MsButton from '@/components/pure/ms-button/index.vue';
  import MsBaseTable from '@/components/pure/ms-table/base-table.vue';
  import { MsTableColumn } from '@/components/pure/ms-table/type';
  import useTable from '@/components/pure/ms-table/useTable';

  import { operationHistory } from '@/api/modules/api-test/management';
  import { operationTypeOptions } from '@/config/common';
  import { useI18n } from '@/hooks/useI18n';
  import useVisit from '@/hooks/useVisit';
  import useAppStore from '@/store/modules/app';

  const props = defineProps<{
    sourceId: string | number;
  }>();

  const appStore = useAppStore();
  const { t } = useI18n();
  const visitedKey = 'messageManagementRobotListTip';
  const { addVisited, getIsVisited } = useVisit(visitedKey);

  const columns: MsTableColumn = [
    {
      title: 'apiTestManagement.changeOrder',
      dataIndex: 'id',
      width: 200,
    },
    {
      title: 'apiTestManagement.type',
      dataIndex: 'type',
      slotName: 'type',
      titleSlotName: 'typeFilter',
      width: 100,
    },
    {
      title: 'mockManagement.operationUser',
      dataIndex: 'createUserName',
      showTooltip: true,
      width: 150,
    },
    {
      title: 'apiTestManagement.updateTime',
      dataIndex: 'createTime',
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 180,
    },
    // {
    //   title: 'common.operation',
    //   slotName: 'action',
    //   dataIndex: 'operation',
    //   width: 50,
    // },
  ];
  const { propsRes, propsEvent, loadList, setLoadListParams } = useTable(
    operationHistory,
    {
      columns,
      scroll: { x: '100%' },
      selectable: false,
      heightUsed: 374,
    },
    (item) => ({
      ...item,
      type: t(operationTypeOptions.find((e) => e.value === item.type)?.label || ''),
      createTime: dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss'),
      updateTime: dayjs(item.updateTime).format('YYYY-MM-DD HH:mm:ss'),
    })
  );

  function loadHistory() {
    setLoadListParams({
      projectId: appStore.currentProjectId,
      sourceId: props.sourceId,
    });
    loadList();
  }

  onBeforeMount(() => {
    loadHistory();
  });

  // async function recover(record: any) {
  //   try {
  //     await recoverOperationHistory({
  //       id: record.id,
  //       sourceId: props.sourceId,
  //     });
  //   } catch (error) {
  //     // eslint-disable-next-line no-console
  //     console.log(error);
  //   }
  // }
</script>

<style lang="less" scoped>
  .history-container {
    @apply h-full overflow-y-auto;

    .ms-scroll-bar();
  }
</style>
