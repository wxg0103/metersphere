<template>
  <div class="history-container">
    <div class="history-table-before">
      <span class="text-[var(--color-text-1)]">{{ t('case.detail.execute.history.list') }}</span>
      <!--      <a-input-search
        v-model:model-value="keyword"
        :placeholder="t('apiTestManagement.quoteSearchPlaceholder')"
        allow-clear
        class="mr-[8px] w-[240px]"
        @search="loadExecuteList"
        @press-enter="loadExecuteList"
      />-->
    </div>
    <ms-base-table v-bind="propsRes" no-disable v-on="propsEvent">
      <template #triggerModeFilter="{ columnConfig }">
        <a-trigger
          v-model:popup-visible="triggerModeFilterVisible"
          trigger="click"
          @popup-visible-change="handleFilterHidden"
        >
          <MsButton type="text" class="arco-btn-text--secondary" @click="triggerModeFilterVisible = true">
            <div class="font-medium">
              {{ t(columnConfig.title as string) }}
            </div>
            <icon-down :class="triggerModeFilterVisible ? 'text-[rgb(var(--primary-5))]' : ''" />
          </MsButton>
          <template #content>
            <div class="arco-table-filters-content">
              <div class="ml-[6px] flex items-center justify-start px-[6px] py-[2px]">
                <a-checkbox-group v-model:model-value="triggerModeListFilters" direction="vertical" size="small">
                  <a-checkbox v-for="(key, value) of TriggerModeLabel" :key="key" :value="value">
                    <div class="font-medium">{{ t(key) }}</div>
                  </a-checkbox>
                </a-checkbox-group>
              </div>
              <div class="filter-button">
                <a-button size="mini" class="mr-[8px]" @click="resetModeFilter">
                  {{ t('common.reset') }}
                </a-button>
                <a-button type="primary" size="mini" @click="handleFilterHidden(false)">
                  {{ t('system.orgTemplate.confirm') }}
                </a-button>
              </div>
            </div>
          </template>
        </a-trigger>
      </template>
      <template #statusFilter="{ columnConfig }">
        <a-trigger
          v-model:popup-visible="statusFilterVisible"
          trigger="click"
          @popup-visible-change="handleFilterHidden"
        >
          <MsButton type="text" class="arco-btn-text--secondary" @click="statusFilterVisible = true">
            {{ t(columnConfig.title as string) }}
            <icon-down :class="statusFilterVisible ? 'text-[rgb(var(--primary-5))]' : ''" />
          </MsButton>
          <template #content>
            <div class="arco-table-filters-content">
              <div class="ml-[6px] flex items-center justify-start px-[6px] py-[2px]">
                <a-checkbox-group v-model:model-value="statusFilters" direction="vertical" size="small">
                  <a-checkbox v-for="val of statusList" :key="val" :value="val">
                    <ExecutionStatus :module-type="ReportEnum.API_REPORT" :status="val" />
                  </a-checkbox>
                </a-checkbox-group>
              </div>
              <div class="filter-button">
                <a-button size="mini" class="mr-[8px]" @click="resetStatusFilter">
                  {{ t('common.reset') }}
                </a-button>
                <a-button type="primary" size="mini" @click="handleFilterHidden(false)">
                  {{ t('system.orgTemplate.confirm') }}
                </a-button>
              </div>
            </div>
          </template>
        </a-trigger>
      </template>
      <template #triggerMode="{ record }">
        <span>{{ t(TriggerModeLabel[record.triggerMode]) }}</span>
      </template>
      <template #status="{ record }">
        <ExecutionStatus :status="record.status" :module-type="ReportEnum.API_REPORT" />
      </template>
      <template #operation="{ record, rowIndex }">
        <a-tooltip :disabled="!record.deleted" :content="t('case.detail.report.delete')" position="top">
          <MsButton :disabled="record.deleted" class="!mr-0" @click="showResult(record, rowIndex)"
            >{{ t('apiScenario.executeHistory.execution.operation') }}
          </MsButton>
        </a-tooltip>
      </template>
    </ms-base-table>
  </div>
  <caseAndScenarioReportDrawer v-model:visible="showResponse" :report-id="activeReportId" />
</template>

<script setup lang="ts">
  import dayjs from 'dayjs';

  import MsButton from '@/components/pure/ms-button/index.vue';
  import MsBaseTable from '@/components/pure/ms-table/base-table.vue';
  import { MsTableColumn } from '@/components/pure/ms-table/type';
  import useTable from '@/components/pure/ms-table/useTable';
  import caseAndScenarioReportDrawer from '@/views/api-test/components/caseAndScenarioReportDrawer.vue';
  import ExecutionStatus from '@/views/api-test/report/component/reportStatus.vue';

  import { getApiCaseExecuteHistory } from '@/api/modules/api-test/management';
  import { useI18n } from '@/hooks/useI18n';
  import useAppStore from '@/store/modules/app';

  import { ApiCaseExecuteHistoryItem } from '@/models/apiTest/management';
  import { ReportEnum, ReportStatus, TriggerModeLabel } from '@/enums/reportEnum';

  const triggerModeListFilters = ref<string[]>();
  const triggerModeFilterVisible = ref(false);
  const statusFilterVisible = ref(false);
  const statusFilters = ref<string[]>();
  const statusList = computed(() => {
    return Object.keys(ReportStatus[ReportEnum.API_REPORT]);
  });

  const showResponse = ref(false);

  const props = defineProps<{
    sourceId: string | number;
    moduleType: string;
    protocol: string;
  }>();

  const appStore = useAppStore();
  const { t } = useI18n();

  const keyword = ref('');

  const columns: MsTableColumn = [
    {
      title: 'apiTestManagement.order',
      dataIndex: 'num',
      slotName: 'num',
      sortIndex: 1,
      width: 150,
    },
    {
      title: 'apiTestManagement.executeMethod',
      dataIndex: 'triggerMode',
      slotName: 'triggerMode',
      titleSlotName: 'triggerModeFilter',
      showTooltip: true,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 150,
    },
    {
      title: 'apiTestManagement.executeResult',
      dataIndex: 'status',
      slotName: 'status',
      titleSlotName: 'statusFilter',
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 150,
    },
    {
      title: 'apiTestManagement.taskOperator',
      dataIndex: 'operationUser',
      width: 100,
    },
    {
      title: 'apiTestManagement.taskOperationTime',
      dataIndex: 'startTime',
      showTooltip: true,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 150,
    },
    {
      title: 'common.operation',
      slotName: 'operation',
      dataIndex: 'operation',
      fixed: 'right',
      showInTable: true,
      showDrag: false,
      width: 150,
    },
  ];
  const { propsRes, propsEvent, loadList, setLoadListParams } = useTable(
    getApiCaseExecuteHistory,
    {
      columns,
      scroll: { x: '100%' },
      selectable: false,
      heightUsed: 374,
    },
    // eslint-disable-next-line no-return-assign
    (item) => ({
      ...item,
      startTime: dayjs(item.startTime).format('YYYY-MM-DD HH:mm:ss'),
    })
  );

  function loadExecuteList() {
    setLoadListParams({
      projectId: appStore.currentProjectId,
      keyword: keyword.value,
      id: props.sourceId,
      filter: {
        triggerMode: triggerModeListFilters.value,
        status: statusFilters.value,
      },
    });
    loadList();
  }

  function handleFilterHidden(val: boolean) {
    if (!val) {
      loadExecuteList();
      triggerModeFilterVisible.value = false;
      statusFilterVisible.value = false;
    }
  }

  function resetModeFilter() {
    triggerModeListFilters.value = [];
    triggerModeFilterVisible.value = false;
    loadExecuteList();
  }

  function resetStatusFilter() {
    statusFilters.value = [];
    statusFilterVisible.value = false;
    loadExecuteList();
  }

  const activeReportIndex = ref<number>(0);
  const activeReportId = ref('');
  async function showResult(record: ApiCaseExecuteHistoryItem, rowIndex: number) {
    activeReportId.value = record.id;
    activeReportIndex.value = rowIndex;
    showResponse.value = true;
  }

  onBeforeMount(() => {
    loadExecuteList();
  });

  defineExpose({
    loadExecuteList,
  });
</script>

<style lang="less" scoped>
  .history-container {
    @apply h-full overflow-y-auto;

    .ms-scroll-bar();
  }
  .history-table-before {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-direction: row;
    margin-bottom: 21px;
  }
</style>
