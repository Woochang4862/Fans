package com.example.jeong_woochang.fans;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ContentActivity extends AppCompatActivity {

    String href;
    String parsing_url = "https://fans.jype.com/";
    //String title = "TITLE", content = "CONTENT";
    //TextView titleView, contentView;
    WebView webView;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        href = getIntent().getStringExtra("href");
        parsing_url = parsing_url + href;

        webView=(WebView)findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(parsing_url);

        finish();
    }
    //    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            Source source;
//            try {
//                System.out.println(parsing_url);
//                URL url = new URL(parsing_url);
//                source = new Source(url);
//
//                List<Element> listDIV = source.getAllElements(HTMLElementName.DIV);
//
//                for(int i=0;i<listDIV.size();i++) {
//                    Element DIV = listDIV.get(i);
//
//                    String DIV_class = DIV.getAttributeValue("class");
//                    String DIV_style = DIV.getAttributeValue("style");
//                    if (DIV_class != null && DIV_style != null) {
//                        if (DIV_class.equalsIgnoreCase("container") && DIV_style.equalsIgnoreCase("height: 100%;")) {
//                            //<DIV class="row>...</DIV> == 트와이스 사진------(0)
//                            //<DIV class="row>...</DIV> == 게시글 관련 내용---(1)
//                            List<Element> listDIV2 = DIV.getAllElements(HTMLElementName.DIV);
//                            for (int k = 0; k < listDIV2.size(); k++) {
//                                Element DIV2 = listDIV2.get(k);
//
//                                String DIV2_class=DIV2.getAttributeValue("class");
//                                if(DIV2_class!=null){
//                                    if(DIV2_class.equalsIgnoreCase("row")&&k>=1){
//                                        //DIV2 == <DIV class="row>...</DIV> == 게시글 관련 내용---(1)
//                                        List<Element> listDIV3 = DIV2.getAllElements(HTMLElementName.DIV);
//                                        for(int n=0;n<listDIV3.size();n++){
//                                            Element DIV3 = listDIV3.get(n);
//
//                                            String DIV3_class=DIV3.getAttributeValue("class");
//                                            if(DIV3_class!=null){
//                                                if(DIV3_class.equalsIgnoreCase("col-lg-10 col-md-10 col-sm-10 col-xs-12")){
//                                                    List<Element> listDIV4 = DIV3.getAllElements(HTMLElementName.DIV);
//                                                    for (int j = 0; j < listDIV4.size(); j++) {
//                                                        Element DIV4 = listDIV4.get(j);
//                                                        switch (j) {
//                                                            case 0:
//                                                            case 2:
//                                                            case 4:
//                                                            case 5:
//                                                            case 6:
//                                                            case 7:
//                                                            case 8:
//                                                                break;
//                                                            case 1:
//                                                                List<Element> listSPAN = DIV4.getAllElements(HTMLElementName.SPAN);
//                                                                Element SPAN = listSPAN.get(2);
//                                                                title = SPAN.getTextExtractor().toString();
//                                                                System.out.println(title);
//                                                                break;
//                                                            case 3:
//                                                                List<Element> listDIV5 = DIV4.getAllElements(HTMLElementName.DIV);
//                                                                Element DIV5 = listDIV5.get(0);
//                                                                List<Element> listP = DIV5.getAllElements(HTMLElementName.P);
//                                                                for (int l = 0; l < listP.size(); l++) {
//                                                                    Element P = listP.get(l);
//                                                                    String tmp_content = P.getTextExtractor().toString();
//                                                                    tmp_content = tmp_content + "\n";
//                                                                    content = content + tmp_content;
//                                                                    System.out.println(content);
//                                                                }
//                                                        }
//                                                    }
//
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                Log.d("error :::: ", e.getMessage());
//            }
//        }
//    };
//
//    Thread thread = new Thread(runnable);
//        try {
//                thread.start();
//                thread.join();
//                } catch(Exception e) {
//
//                }
}