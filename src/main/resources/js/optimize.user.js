// ==UserScript==
// @name         去广告/去弹窗/优化
// @namespace    http://tampermonkey.net/
// @version      1.6
// @description  去掉各个网站的登录弹窗等辣鸡信息，优化某些功能
// @author       XanderYe
// @require      http://lib.baomitu.com/clipboard.js/1.7.1/clipboard.min.js
// @require      https://lib.baomitu.com/jquery/3.5.0/jquery.min.js
// @updateURL    https://github.com/XanderYe/xander-tool/raw/master/src/main/resources/js/optimize.user.js
// @supportURL   https://www.xanderye.cn/
// @match        http*://www.zhihu.com/question/*
// @match        http*://*.csdn.net/*
// @match        http*://www.iqiyi.com/*.html
// @match        http*://www.bilibili.com/*
// @match        http*://*.zmlearn.com/page/detail/*
// @match        http*://bbs.hupu.com/*.html
// @match        http*://jingyan.baidu.com/article/*
// @match        http*://dnf.qq.com/gift.shtml
// @match        http*://www.wcmdd.top/dnf/all
// @match        http*://bbs.colg.cn/thread-*.html
// ==/UserScript==

var jQ = $.noConflict(true);
jQ(function($){
    var newStyle = document.createElement("style");
    var newNode;
    if (website("zhihu")) {
        // 知乎干掉登录弹窗、推荐图书
        newNode = document.createTextNode("html {overflow: auto !important;} .Modal-backdrop, .MCNLinkCard {display: none !important} .Modal-closeIcon {fill: #8590a6 !important}");
        // 监控登录窗，干掉2次弹窗
        var signFlowModal = null;
        var count = 0;
        var interval = setInterval(function () {
            signFlowModal = $("body .signFlowModal");
            if (signFlowModal.length > 0) {
                signFlowModal.parent().parent().remove();
                count++;
            }
            if (count == 2) {
                clearInterval(interval);
            }
        },100);
        // 知乎不加载图片的问题
        var images = $("img");
        for(var i = 0; i < images.length; i++) {
            var src = images.eq(i).attr("src");
            if(src.indexOf("data") > -1) {
                images.eq(i).attr("src", images.eq(i).attr("data-actualsrc"));
            }
        }
    } else if (website("csdn")) {
        // CSDN
        newNode = document.createTextNode(".login-mark,.login-box,.leftPop,.opt-box {display: none !important} .comment-list-box {max-height: none !important} .htmledit_views code ol li{height: 26px !important}");
    } else if (website("iqiyi")) {
        // 爱奇艺
        newNode = document.createTextNode("div[templatetype=common_pause] {display: none !important}");
    } else if (website("bilibili")) {
        // B站
        newNode = document.createTextNode(".gg-floor-module, #slide_ad, .gg-window .operate-card,.banner-card.b-wrap:nth-of-type(1) {display: none !important}");
    } else if (website("zmlearn")) {
        // 掌门添加图片放大查看功能
        $("#app").append($("<div></div>").attr("id", "showImage").css("display", "none"))
        $("#showImage").bind("click", function() {
            $("#showImage").hide();
        })
        var imgEle = $("<img />").attr("id", "imgEle");
        $("#showImage").append(imgEle);
        var classStr = ".moreImgs, .oneImgs, .img-pool img,  .imgShow img";
        newNode = document.createTextNode(classStr + "{cursor: zoom-in;} #showImage {display: none; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.3); cursor: zoom-out} #imageEle {position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); cursor: zoom-out;} .downLoad {display: none !important}");
        setTimeout(function(){
            $(classStr).bind("click", function(){
                var backgroundImage = $(this).css("backgroundImage");
                var imgUrl = backgroundImage && backgroundImage != "none" ? backgroundImage.substring(5,backgroundImage.length - 2) : $(this).attr("src");
                imgEle.attr("src", imgUrl);
                var imgWidth = imgEle[0].width;
                var imgHeight = imgEle[0].height;
                console.log(window.innerWidth / window.innerHeight)
                if (imgWidth / imgHeight < window.innerWidth / window.innerHeight) {
                    $("#imgEle").css("width", "auto");
                    $("#imgEle").css("height", Math.min(imgHeight, window.innerHeight) + "px");
                } else {
                    $("#imgEle").css("width", Math.min(imgWidth, window.innerWidth) + "px");
                    $("#imgEle").css("height", "auto");
                }
               $("#showImage").show();
            })
        },500)
    } else if (website("hupu")) {
        // 虎扑去除方向键上下页
        document.onkeydown = function(){}
    } else if (website("jingyan.baidu")) {
        // 关闭百度经验浮动视频
        $(".video-originate-container").remove();
    } else if (website("dnf.qq.com")) {
        newNode = document.createTextNode("#cpybtn {cursor: pointer}")
        $("#logined").append("<a id='cpybtn'  data-clipboard-text='" + document.cookie + "'>复制cookie</a>");
        var clipboard = new Clipboard('#cpybtn');
        clipboard.on('success', function(e) {
            alert("复制成功");
        });
        clipboard.on('error', function(e) {
            console.log(e);
        });
    } else if (website("wcmdd.top")) {
        // dnf装备统计自动切换用户
        setTimeout(function(){
            setUserName = function(un) {
                $('#userNameText').html(un);
                localStorage.setItem("userName", un);
                setCookie("userName", un);
            }
            var localName = localStorage.getItem("userName");
            var cookieName = getCookie("userName");
            if(localName && localName.toLowerCase() != cookieName.toLowerCase()) {
                var param = {userNameAno: localName};
                $.ajax({
                    "type": "post",
                    "url": '/dnf/user/changeUser',
                    "Content-Type": "application/json;",
                    "data": param,
                    "success": function (resp) {
                        if (resp.success) {
                            setUserName(localName);
                            window.location.reload();
                        } else {
                            alert(resp.message);
                        }
                    }
                });
            }
            var total = $(".mr").length - 35 - 6;
            var selected = $(".selectedZB").length;
            $(".zhuangbei .zhuangbeidiv").eq(0).append("<span style='margin-left: 10px;'>" + selected + "/" + total + "</span>")
        }, 1000)
    } else if (website("colg.cn")) {
        var imgs = $(".guestviewthumb_cur");
        for (i = 0; i < imgs.length; i++) {
            imgs.eq(i).attr("width", "");
            imgs.eq(i).css("max-width", "none");
        }
    }
    if (newNode !== undefined) {
        newStyle.appendChild(newNode);
        document.head.appendChild(newStyle);
    }

    function website(keyword) {
        return location.host.indexOf(keyword) > -1;
    }
})