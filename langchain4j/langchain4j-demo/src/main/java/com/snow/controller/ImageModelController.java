// NOT RUN!

package com.snow.controller;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
@Slf4j
public class ImageModelController {
    // 使用通义千问实现图像理解 //
    @jakarta.annotation.Resource(name = "qwen-vl")
    private ChatModel chatModel;

    @Value("classpath:static/images/mi.jpg")
    // import org.springframework.core.io.Resource;
    private Resource resource;

    // 通过Base64编码将图片转化为字符串，结合ImageContent和TextContent形成UserMessage一起发送到模型进行处理。
    @GetMapping(value = "/image/call")
    public String readImageContent() throws IOException
    {
        //第一步，图片转码：通过Base64编码将图片转化为字符串
        byte[] byteArray = resource.getContentAsByteArray();
        String base64Data = Base64.getEncoder().encodeToString(byteArray);

        //第二步，提示词指定：结合ImageContent和TextContent一起发送到模型进行处理。
        UserMessage userMessage = UserMessage.from(
                TextContent.from("从下面图片种获取来源网站名称，股价走势和5月30号股价"),
                ImageContent.from(base64Data, "image/jpg")
        );
        //第三步，API调用：使用OpenAiChatModel来构建请求，并通过chat()方法调用模型。
        //请求内容包括文本提示和图片，模型会根据输入返回分析结果。
        ChatResponse chatResponse = chatModel.chat(userMessage);

        //第四步，解析与输出：从ChatResponse中获取AI大模型的回复，打印出处理后的结果。
        String result = chatResponse.aiMessage().text();

        //后台打印
        System.out.println(result);

        //返回前台
        return result;
    }


    // 使用通义万象实现文生图 //
    @Autowired
    private WanxImageModel wanxImageModel;

    @GetMapping(value = "/image/create2")
    public String createImageContent2()
    {
        System.out.println(wanxImageModel);

        Response<Image> imageResponse = wanxImageModel.generate("美女");
        System.out.println(imageResponse.content().url());

        return imageResponse.content().url().toString();
    }

    // ImageSynthesisResult ImageSynthesis::call(ImageSynthesisParam)
    @GetMapping(value = "/image/create3")
    public String createImageContent3() {
        String prompt = "近景镜头，18岁的中国女孩，古代服饰，圆脸，正面看着镜头，民族优雅的服装，商业摄影，室外，电影级光照，半身特写，精致的淡妆，锐利的边缘。";

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(System.getenv("aliQwen-api"))
                        .model(ImageSynthesis.Models.WANX_V1)
                        .prompt(prompt)
                        .style("<watercolor>")
                        .n(1)
                        .size("1024*1024")
                        .build();
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;

        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }

        System.out.println(JsonUtils.toJson(result));
        return JsonUtils.toJson(result);
    }
}
