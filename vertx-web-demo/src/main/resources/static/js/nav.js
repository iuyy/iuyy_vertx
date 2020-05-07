/**
 * Created by iuyy on 2018/5/19.
 * 侧面导航的效果
 */
$(document).ready(function() {
    $("#main-nav li ul").hide();                                                // 隐藏所有子菜单
    $("#main-nav li a.current").parent().find("ul").slideToggle("slow");        // 打开当前菜单的子菜单

    $("#main-nav li a.iuyy-nav-item").click(                                     // 当最顶层菜单被点击
        function () {
            $("#main-nav li a.iuyy-nav-item").removeClass("active");
            $(this).addClass("active");
            $(this).parent().siblings().find("ul").slideUp("normal");           // 被点击菜单上方的菜单向上滑动
            $(this).next().slideToggle("normal");                               // 被点击菜单下方的菜单向下滑动
            return false;
        }
    );

    $("#main-nav li a.no-submenu").click(                                       // 当点击没有子菜单的选项时
        function () {
            window.location.href = (this.href);                                 // 打开链接
            return false;
        }
    );

    $("#main-nav li .iuyy-nav-item").hover(
        function () {
            $(this).stop().animate({paddingRight: "20px"}, 200);
        },
        function () {
            $(this).stop().animate({paddingRight: "10px"});
        }
    );

});
