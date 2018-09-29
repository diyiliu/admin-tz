window.CM = {
    config: {
        'navbarHeight' : 65,
        'restoreMenuState' : true
    },

    init: function () {
        $('body').append('<div id="cm-menu-backdrop" class="visible-xs-block visible-sm-block"></div>' +
            '<div id="cm-submenu-popover" class="dropdown">' +
            '<div data-toggle="dropdown" id="dropdownMenuButton"></div>' +
            '<div class="popover cm-popover right dropdown-menu" aria-labelledby="dropdownMenuButton">' +
            '<div class="arrow"></div>' +
            '<ul></ul>' +
            '</div>' +
            '</div>');

        var state = CM.getState();
        if (state.mobile || ((this.config.restoreMenuState) && ($.cookie('cm-menu-toggled') == 'true'))) {
            $("#main-wrapper").addClass("mini-sidebar");
            $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");
            $(".sidebartoggler").prop("checked", !0);

            // 中屏
            if(!state.md){
                $('.cm-submenu').find('a.active').next().removeClass("in");
            }
        } else {
            $(".sidebartoggler").prop("checked", !1);
            $("#main-wrapper").attr("data-sidebartype", "full");
        }

        this.menu.init();

        var self = this;
        $('.modal').on('show.bs.modal', function(){
            self.preventScroll.enable();
            var $this = $(this);
            var $modal_dialog = $this.find('.modal-dialog');
            // 关键代码，如没将modal设置为 block，则$modala_dialog.height() 为零
            $this.css('display', 'block');
            $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) });
        });
        $('.modal').on('hidden.bs.modal', function(){
            self.preventScroll.disable();
        });
    },

    menu: {
        init: function () {
            var self = this;

            $("[data-toggle='cm-menu']").click(this.toggle);
            $('#cm-menu a').click(function () {
                var href = $(this).attr('href');
                if (href) {
                    if (state.mobile) {
                        $('body').removeClass('cm-menu-toggled');
                        $.cookie('cm-menu-toggled', false, {path: '/'});
                    }
                    if (!$(this).parents('.cm-submenu').size()) {
                        $('.cm-menu-items li').removeAttr('style');
                        $('.cm-submenu').removeClass('open');
                    }
                }
            });

            $('.cm-submenu').click(function (e, notrans, nopopo) {
                var m = $(this);
                var state = CM.getState();

                // 宽屏
                if ((!state.mobile) && (!state.open)) {
                    self.setPopover(m);
                    return false;
                }

                // 中屏
                if (state.mobile && state.open && !state.md) {
                    self.toggle();
                    return false;
                }
            });

            var state = CM.getState();
            if ((!state.mobile) && (!state.open)) {
                $('.cm-submenu.pre-open').removeClass('pre-open');
            } else {
                var po = $('.cm-submenu.pre-open');
                po.nextAll().css('transform', 'translateY(' + po.children('ul').height() + 'px)');
                po.addClass('open').removeClass('pre-open');
            }
        },

        hidePopover: function () {
            $("#cm-submenu-popover [data-toggle='dropdown']").dropdown('toggle');
        },

        setPopover: function (li) {
            li.find('a').removeClass("active");
            li.find('ul').removeClass("in");

            var p = $('#cm-submenu-popover');
            var open = p.hasClass('show');
            var popen = li.hasClass('popen');
            $('.cm-submenu').removeClass('popen');
            if (popen && open) {
                this.hidePopover();
                return true;
            }

            $('#cm-submenu-popover ul').html(li.find('ul').html());
            var m = 10;
            var d = $(window).height() - m;
            var a = $('#cm-submenu-popover').find('.arrow');
            var h = p.find('.cm-popover').height();
            var y = li.position().top + CM.config.navbarHeight * 1.5  - h / 2;
            var x = y + h;
            a.show();
            if (x > d) {
                var o = x - d;
                y -= o;
                a.css('top', h / 2 + o);
            }
            else if (y < m) {
                var o = y - m;
                y -= o;
                a.css('top', h / 2 + o);
            }
            else {
                a.css('top', '50%');
            }
            if (a.position().top > h) {
                a.hide();
            }
            p.css('top', y);
            li.addClass('popen');
            if (!open){
                $("#cm-submenu-popover [data-toggle='dropdown']").click();
            }
        },


        toggle: function () {
            $("#main-wrapper").toggleClass("mini-sidebar");

            if ($("#main-wrapper").hasClass("mini-sidebar")) {
                $(".sidebartoggler").prop("checked", !0);
                $("#main-wrapper").attr("data-sidebartype", "mini-sidebar");

                $('.cm-submenu').find('a.active').next().removeClass("in");
            } else {
                $(".sidebartoggler").prop("checked", !1);
                $("#main-wrapper").attr("data-sidebartype", "full");

                $('.cm-submenu').find('a.active').next().addClass("in");
            }

            $(window).resize();
            var state = CM.getState();
            $.cookie('cm-menu-toggled', (!state.open), {path: '/'});
        }
    },

    getState: function () {
        var state = {};
        state.mobile = ($('#cm-menu-backdrop').css('display') == 'block');
        state.open = (state.mobile == $("#main-wrapper").hasClass("mini-sidebar"));
        state.md = ($('#cm-mobile').css('display') == 'block');
        return state;
    },

    preventScroll: {
        s: -1,
        enable: function(){
            this.s = $(document).scrollTop();

            var f = $('.footer');
            var x = $(window).height() + this.s - f.position().top - CM.config.navbarHeight;
            f.css('bottom', x + 'px');
            $('#main-wrapper').addClass('prevent-scroll').css('margin-top', '-' + this.s + 'px');
        },
        disable: function(){
            $("#main-wrapper").removeAttr('style').removeClass('prevent-scroll');
            $('.footer').removeAttr('style');
            if(this.s != -1) $(document).scrollTop(this.s);
        }
    },
};

$(function () {
    CM.init();
});

function alertTip(type, msg) {
    if ('success' == type){
        toastr.success(msg, '提示', {
            timeOut: 3000,
            "closeButton": true,
            "progressBar": true
        });
        return;
    }

    if ('error' == type){
        toastr.error(msg, '错误', {
            timeOut: 3000,
            "closeButton": true,
            "progressBar": true
        });
        return;
    }
}

/*
function doToast(type, msg) {
    if ('success' == type){
        $.toast({
            heading: '提示',
            text: msg,
            position: 'top-right',
            loaderBg: '#fff',
            icon: 'success',
            hideAfter: 3500,
            stack: 6
        });
        return;
    }

    if ('error' == type){
        $.toast({
            heading: '错误',
            text: msg,
            position: 'top-right',
            loaderBg: '#fff',
            icon: 'error',
            hideAfter: 3500,
            stack: 6
        });
        return;
    }
}
*/
