<template>
  <MsCard simple no-content-padding>
    <MsSplitBox v-model:width="leftWidth" @expand-change="handleCollapse">
      <template #first>
        <UserGroupLeft
          ref="ugLeftRef"
          :add-permission="['SYSTEM_USER_ROLE:READ+ADD']"
          :update-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
          :is-global-disable="false"
          @handle-select="handleSelect"
          @add-user-success="handleAddMember"
        />
      </template>
      <template #second>
        <div class="h-full pt-[24px]">
          <div class="flex flex-row items-center justify-between px-[24px]">
            <a-tooltip :content="currentUserGroupItem.name">
              <div class="one-line-text max-w-[300px] font-medium">{{ currentUserGroupItem.name }}</div>
            </a-tooltip>
            <div class="flex items-center">
              <a-input-search
                v-if="currentTable === 'user'"
                :placeholder="t('system.user.searchUser')"
                class="w-[240px]"
                allow-clear
                @press-enter="handleEnter"
                @search="handleSearch"
                @clear="handleSearch('')"
              ></a-input-search>
              <a-radio-group
                v-if="couldShowUser && couldShowAuth"
                v-model="currentTable"
                class="ml-[14px]"
                type="button"
              >
                <a-radio v-if="couldShowAuth" value="auth">{{ t('system.userGroup.auth') }}</a-radio>
                <a-radio v-if="couldShowUser" value="user">{{ t('system.userGroup.user') }}</a-radio>
              </a-radio-group>
            </div>
          </div>
          <div class="mt-[16px]">
            <UserTable
              v-if="currentTable === 'user' && couldShowUser"
              ref="userRef"
              :keyword="currentKeyword"
              :current="currentUserGroupItem"
              :delete-permission="['SYSTEM_USER_ROLE:READ+DELETE']"
              :read-permission="['SYSTEM_USER_ROLE:READ']"
              :update-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
            />
            <AuthTable
              v-if="currentTable === 'auth' && couldShowAuth"
              :current="currentUserGroupItem"
              :save-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
              :disabled="!hasAnyPermission(['SYSTEM_USER_ROLE:READ+UPDATE'])"
            />
          </div>
        </div>
      </template>
    </MsSplitBox>
  </MsCard>
</template>

<script lang="ts" setup>
  /**
   * @description 系统设置-系统-用户组
   */
  import { useRouter } from 'vue-router';

  import MsCard from '@/components/pure/ms-card/index.vue';
  import MsSplitBox from '@/components/pure/ms-split-box/index.vue';
  import AuthTable from '@/components/business/ms-user-group-comp/authTable.vue';
  import UserGroupLeft from '@/components/business/ms-user-group-comp/msUserGroupLeft.vue';
  import UserTable from '@/components/business/ms-user-group-comp/userTable.vue';

  import { useI18n } from '@/hooks/useI18n';
  import { hasAnyPermission } from '@/utils/permission';

  import { CurrentUserGroupItem } from '@/models/setting/usergroup';
  import { AuthScopeEnum } from '@/enums/commonEnum';

  const currentTable = ref('auth');
  provide('systemType', AuthScopeEnum.SYSTEM);
  const router = useRouter();
  const { t } = useI18n();
  const currentKeyword = ref('');
  const ugLeftRef = ref();

  const currentUserGroupItem = ref<CurrentUserGroupItem>({
    id: '',
    name: '',
    type: AuthScopeEnum.SYSTEM,
    internal: true,
  });

  const userRef = ref();
  const leftCollapse = ref(true);
  const leftWidth = ref('300px');

  const tableSearch = () => {
    if (currentTable.value === 'user' && userRef.value) {
      userRef.value.fetchData();
    } else if (!userRef.value) {
      nextTick(() => {
        userRef.value.fetchData();
      });
    }
  };

  const handleSearch = (value: string) => {
    currentKeyword.value = value;
    tableSearch();
  };
  const handleEnter = (eve: Event) => {
    currentKeyword.value = (eve.target as HTMLInputElement).value;
    tableSearch();
  };

  const handleSelect = (item: CurrentUserGroupItem) => {
    currentUserGroupItem.value = item;
  };

  const handleAddMember = (id: string) => {
    if (id === currentUserGroupItem.value.id) {
      tableSearch();
    }
  };

  const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.SYSTEM);
  const couldShowAuth = computed(() => currentUserGroupItem.value.id !== 'admin');
  const handleCollapse = (collapse: boolean) => {
    leftCollapse.value = collapse;
    if (collapse) {
      leftWidth.value = '300px';
      nextTick(() => {
        ugLeftRef.value?.initData(currentUserGroupItem.value.id);
      });
    }
  };
  watchEffect(() => {
    if (!couldShowAuth.value) {
      currentTable.value = 'user';
    } else if (!couldShowUser.value) {
      currentTable.value = 'auth';
    } else {
      currentTable.value = 'auth';
    }
  });
  onMounted(() => {
    ugLeftRef.value?.initData(router.currentRoute.value.query.id, true);
  });
</script>

<style lang="less" scoped></style>
