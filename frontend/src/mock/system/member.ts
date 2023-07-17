import { mock } from '@/utils/setup-mock';
import { RequestEnum } from '@/enums/httpEnum';

const getMemberList = () => {
  return [
    {
      id: '103423',
      name: '成员1',
      email: 'dehihu@kds.sd',
      enable: true,
      createTime: 1686905750716,
      updateTime: 0,
      lastOrganizationId: 'string',
      phone: '18473647583',
      source: 'string',
      lastProjectId: 'string',
      createUser: 'string',
      updateUser: 'string',
      projectList: [
        {
          id: 'string',
          num: 0,
          name: '项目 1',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
        {
          id: 'string',
          num: 0,
          name: '项目 2',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
        {
          id: 'string',
          num: 0,
          name: '项目 3',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
        {
          id: 'string',
          num: 0,
          name: '项目 4',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
      ],
      userRoleList: [
        {
          id: 'string',
          name: '角色 1',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
        {
          id: 'string',
          name: '角色 2',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
        {
          id: 'string',
          name: '角色 3',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
        {
          id: 'string',
          name: '角色 4',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
      ],
    },
    {
      id: '103233',
      name: '成员2',
      email: 'dehihu@kds.sd',
      enable: true,
      createTime: 1686905750716,
      updateTime: 0,
      lastOrganizationId: 'string',
      phone: '18473647583',
      source: 'string',
      lastProjectId: 'string',
      createUser: 'string',
      updateUser: 'string',
      projectList: [
        {
          id: 'string',
          num: 0,
          name: '项目 1',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
        {
          id: 'string',
          num: 0,
          name: '项目 2',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
        {
          id: 'string',
          num: 0,
          name: '组织 3',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
        {
          id: 'string',
          num: 0,
          name: '项目 4',
          description: 'blabla',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          updateUser: 'string',
          deleted: true,
          deleteUser: 'string',
          deleteTime: 0,
          enable: true,
        },
      ],
      userRoleList: [
        {
          id: 'string',
          name: '角色 1',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
        {
          id: 'string',
          name: '角色 2',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
        {
          id: 'string',
          name: '角色 3',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
        {
          id: 'string',
          name: '角色 4',
          description: 'sdadasd',
          internal: true,
          type: 'string',
          createTime: 0,
          updateTime: 0,
          createUser: 'string',
          scopeId: 'string',
          pos: 0,
        },
      ],
    },
  ];
};

mock(RequestEnum.POST, '/member/page', getMemberList(), 200, true);
