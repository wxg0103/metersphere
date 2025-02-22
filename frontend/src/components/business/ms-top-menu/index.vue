<template>
  <a-menu
    v-show="appStore.topMenus.length > 0"
    v-model:selected-keys="activeMenus"
    class="bg-transparent"
    mode="horizontal"
    @menu-item-click="menuClickHandler"
  >
    <a-menu-item v-for="menu of appStore.topMenus" :key="(menu.name as string)" @click="jumpPath(menu.name)">
      {{ t(menu.meta?.locale || '') }}
    </a-menu-item>
  </a-menu>
</template>

<script setup lang="ts">
  import { Ref, ref, watch } from 'vue';
  import { RouteRecordName, RouteRecordRaw, useRouter } from 'vue-router';
  import { cloneDeep } from 'lodash-es';

  import { useI18n } from '@/hooks/useI18n';
  import usePermission from '@/hooks/usePermission';
  import appClientMenus from '@/router/app-menus';
  import { useAppStore } from '@/store';
  import useLicenseStore from '@/store/modules/setting/license';
  import { listenerRouteChange } from '@/utils/route-listener';

  import { RouteEnum } from '@/enums/routeEnum';

  const licenseStore = useLicenseStore();
  const copyRouters = cloneDeep(appClientMenus) as RouteRecordRaw[];
  const permission = usePermission();
  const appStore = useAppStore();
  const router = useRouter();
  const { t } = useI18n();
  const activeMenus: Ref<RouteRecordName[]> = ref([]);

  function checkAuthMenu() {
    const topMenus = appStore.getTopMenus;
    if (appStore.packageType === 'community') {
      appStore.setTopMenus(topMenus.filter((item) => item.name !== RouteEnum.SETTING_SYSTEM_AUTHORIZED_MANAGEMENT));
    } else {
      appStore.setTopMenus(topMenus);
    }
  }

  watch(
    () => appStore.getCurrentTopMenu?.name,
    (val) => {
      checkAuthMenu();
      activeMenus.value = [val || ''];
    },
    {
      immediate: true,
    }
  );

  function setCurrentTopMenu(key: string) {
    // 先判断全等，避免同级路由出现命名包含情况
    const secParentFullSame = appStore.topMenus.find((route: RouteRecordRaw) => {
      return key === route?.name;
    });

    // 非全等的情况下，一定是父子路由包含关系
    const secParentLike = appStore.topMenus.find((route: RouteRecordRaw) => {
      return key.includes(route?.name as string);
    });

    if (secParentFullSame) {
      appStore.setCurrentTopMenu(secParentFullSame);
    } else if (secParentLike) {
      appStore.setCurrentTopMenu(secParentLike);
    }
  }

  /**
   * 监听路由变化，存储打开的顶部菜单
   */
  listenerRouteChange((newRoute) => {
    const { name } = newRoute;
    for (let i = 0; i < copyRouters.length; i++) {
      const firstRoute = copyRouters[i];
      // 权限校验通过
      if (permission.accessRouter(firstRoute)) {
        if (name && firstRoute?.name && (name as string).includes(firstRoute.name as string)) {
          // 先判断二级菜单是否顶部菜单
          let currentParent = firstRoute?.children?.some((item) => item.meta?.isTopMenu)
            ? (firstRoute as RouteRecordRaw)
            : undefined;

          if (!currentParent) {
            // 二级菜单非顶部菜单，则判断三级菜单是否有顶部菜单
            currentParent = firstRoute?.children?.find(
              (item) => name && item?.name && (name as string).includes(item.name as string)
            );
          }

          const filterMenuTopRouter =
            currentParent?.children?.filter((item: any) => permission.accessRouter(item) && item.meta?.isTopMenu) || [];

          appStore.setTopMenus(filterMenuTopRouter);
          setCurrentTopMenu(name as string);

          return;
        }
      }
    }
    // 切换到没有顶部菜单的路由时，清空顶部菜单
    appStore.setTopMenus([]);
    setCurrentTopMenu('');
  }, true);

  function jumpPath(route: RouteRecordName | undefined) {
    router.push({ name: route });
  }
  function menuClickHandler() {
    activeMenus.value = [appStore.getCurrentTopMenu?.name || ''];
  }

  watch(
    () => appStore.currentOrgId,
    async () => {
      await appStore.initSystemPackage();
      if (appStore.packageType === 'enterprise') {
        licenseStore.getValidateLicense();
      }
    },
    {
      immediate: true,
    }
  );

  watch(
    () => appStore.packageType,
    (val) => {
      checkAuthMenu();
    }
  );
</script>

<style lang="less" scoped>
  :deep(.arco-menu-inner) {
    overflow-y: hidden;
    padding: 9px 0;
    .arco-menu-selected-label {
      bottom: -8px !important;
    }
  }
</style>
