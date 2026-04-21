package com.snow.controller;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioModelController {

    private static final String TEXT = "床前明月光， 疑是地上霜。 举头望明月， 低头思故乡。";
//    private static final String PATH = "D:\\Develop\\AIModels\\spring-ai-alibaba\\spring-ai-alibaba-demo\\src\\main\\resources\\tts";

    @Autowired
    private DashScopeAudioSpeechModel audioSpeechModel;

    // 同步调用
    // http://127.0.0.1:18080/audio/sync
    @RequestMapping("/audio/sync")
    public String syncTTS(@RequestParam(value = "msg", defaultValue = TEXT) String message) {
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
//                .model("cosyvoice-v1")
//                .voice("longxiaochun_v2")
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(message, options);
        TextToSpeechResponse response = audioSpeechModel.call(prompt);

        byte[] audioData = response.getResult().getOutput();
        // 保存文件...
        return "ok";
    }
}
