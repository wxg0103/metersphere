<template>
  <div v-if="showImg">
    <div class="mb-[8px] flex items-center gap-[16px]">
      <a-button type="outline" class="arco-btn-outline--secondary" size="mini" @click="handleDownload">
        {{ t('common.download') }}
      </a-button>
      <a-radio-group v-model:model-value="showType" type="button" size="small">
        <a-radio value="image">{{ t('common.image') }}</a-radio>
        <a-radio value="text">{{ t('common.text') }}</a-radio>
      </a-radio-group>
    </div>
    <a-image v-show="showType === 'image'" :src="imageUrl"></a-image>
  </div>
  <MsCodeEditor
    v-show="!showImg || showType === 'text'"
    ref="responseEditorRef"
    :model-value="props.requestResult?.responseResult.body || ''"
    :language="responseLanguage"
    theme="vs"
    :height="showImg ? 'calc(100% - 26px)' : '100%'"
    :languages="[LanguageEnum.JSON, LanguageEnum.HTML, LanguageEnum.XML, LanguageEnum.PLAINTEXT]"
    :show-full-screen="false"
    :show-theme-change="false"
    show-language-change
    show-charset-change
    read-only
  >
    <template #rightTitle>
      <a-button type="outline" class="arco-btn-outline--secondary p-[0_8px]" size="mini" @click="emits('copy')">
        <template #icon>
          <MsIcon type="icon-icon_copy_outlined" class="text-var(--color-text-4)" size="12" />
        </template>
      </a-button>
    </template>
  </MsCodeEditor>
</template>

<script setup lang="ts">
  import MsCodeEditor from '@/components/pure/ms-code-editor/index.vue';
  import { LanguageEnum } from '@/components/pure/ms-code-editor/types';
  import MsIcon from '@/components/pure/ms-icon-font/index.vue';

  import { useI18n } from '@/hooks/useI18n';
  import { downloadUrlFile } from '@/utils';

  import { RequestResult } from '@/models/apiTest/common';

  const props = defineProps<{
    requestResult?: RequestResult;
    requestUrl?: string;
    isHttpProtocol?: boolean;
    isDefinition?: boolean;
  }>();

  const emits = defineEmits<{
    (e: 'copy'): void;
  }>();

  const { t } = useI18n();

  // 响应体语言类型
  const responseLanguage = computed(() => {
    if (props.requestResult) {
      const { contentType } = props.requestResult.responseResult;
      if (contentType.includes('json')) {
        return LanguageEnum.JSON;
      }
      if (contentType.includes('html')) {
        return LanguageEnum.HTML;
      }
      if (contentType.includes('xml')) {
        return LanguageEnum.XML;
      }
    }
    return LanguageEnum.PLAINTEXT;
  });

  const showImg = computed(() => {
    if (props.requestResult) {
      return props.requestResult.responseResult.contentType.includes('image');
    }
    return false;
  });
  const imageUrl = computed(() => {
    if (props.requestResult) {
      return `data:${props.requestResult?.responseResult.contentType};base64,${props.requestResult?.responseResult.imageUrl}`;
    }
    return '';
  });

  const showType = ref<'image' | 'text'>('image');

  function handleDownload() {
    if (imageUrl.value) {
      downloadUrlFile(imageUrl.value, `response.${props.requestResult?.responseResult.contentType.split('/')[1]}`);
    }
  }
</script>

<style scoped></style>
