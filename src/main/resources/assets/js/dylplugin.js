window.DP = {
    config: {
        'showMd': 'myModal',
        'bsTable': 'myTable',
        'addBtn': 'addItem',
        'editBtn': 'editItem',
        'delBtn': 'delItem',
        'saveBtn': 'saveItem',
        'searchFm': 'searchFm',
        'addBefore': function (e) {

        },
        'addAfter': function (e) {

        },
        'editBefore': function (e) {

        },
        'editAfter': function (e) {

        }
    },


    init: function (config) {
        if (typeof config != 'undefined') {
            for (var attr in config) {
                this.config[attr] = config[attr];
            }
        }
        var self = this;

        var $add = $('#' + DP.config.addBtn);
        $add.on('click', function () {
            var t = $(this).data('title');

            self.add(t);
        });

        var $del = $('#' + DP.config.delBtn);
        $del.on('click', function () {
            var u = $(this).data('url');

            self.delete(u);
        });

        var $edit = $('#' + DP.config.editBtn);
        $edit.on('click', function () {
            var t = $(this).data('title');

            self.edit(t)
        });

        var $save = $('#' + DP.config.saveBtn);
        $save.on('click', function () {
            var u = $(this).data('url');

            self.save(u);
        });
    },

    add: function (t) {
        var $md = $('#' + DP.config.showMd);
        $md.find('.modal-title').html(t);
        var $fm = $md.find('form');
        $fm[0].reset();

        DP.config.addBefore();
        $md.modal('show');
        DP.config.addAfter();
    },

    delete: function (u) {
        $table = DP.getTable();
        var checked = $table.bootstrapTable('getSelections');
        if (checked.length < 1) {
            swal("请至少选择一条进行删除!");

            return;
        }

        // 提示
        swal({
                title: "确认删除?",
                text: "删除后将无法恢复",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#e67e22",
                confirmButtonText: "删除",
                closeOnConfirm: true
            },
            function () {
                var idArr = [];
                for (var i = 0; i < checked.length; i++) {
                    idArr[i] = checked[i]['id'];
                }

                $.ajax({
                    type: 'delete',
                    url: u,
                    contentType: "application/json;charset=utf-8",
                    data: JSON.stringify(idArr),
                    dataType: "json",
                    success: function (result) {
                        if (result == 1) {
                            alertTip('success', '删除成功');

                            $table.bootstrapTable('refresh');
                        } else {
                            alertTip('error', '删除失败');
                        }
                    }
                });
            });
    },

    edit: function (t) {
        $table = DP.getTable();
        var checked = $table.bootstrapTable('getSelections');
        if (checked.length != 1) {
            swal("请选择一条进行修改!");

            return;
        }
        var i = checked[0]['id'];

        var $md = $('#' + DP.config.showMd);
        $md.find('.modal-title').html(t);
        var $fm = $md.find('form');
        $fm[0].reset();

        DP.config.editBefore(i);
        $md.modal('show');
        DP.config.editAfter(i);
    },

    save: function (u) {
        var $md = $('#' + DP.config.showMd);
        var $fm = $md.find('form');
        var param = $fm.serialize();

        $.ajax({
            type: 'post',
            url: u,
            data: param,
            dataType: "json",
            beforeSend: function(){
                $('.fakeloader').fadeIn();
            },
            complete: function(){
                $('.fakeloader').fadeOut();
            },
            success: function (result) {
                if (result == 1) {
                    alertTip('success', '保存成功');
                    $md.modal('hide');
                    DP.getTable().bootstrapTable("refresh");
                } else {
                    alertTip('error', '保存失败');
                }
            },
            error: function () {
                alertTip('error', '保存失败');
            }
        });
    },

    delLink: function (a) {
        var id = $(a).data('id');
        var u = $(a).data('url') + id;

        // 提示
        swal({
                title: "确认删除?",
                text: "删除后将无法恢复",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#e67e22",
                confirmButtonText: "删除",
                closeOnConfirm: true
            },
            function () {
                $.ajax({
                    type: 'delete',
                    url: u,
                    dataType: "json",
                    success: function (result) {
                        if (result == 1) {
                            alertTip('success', '删除成功');
                            DP.getTable().bootstrapTable("refresh");
                        } else {
                            alertTip('error', '删除失败');
                        }
                    }, error: function () {
                        alertTip('error', '删除失败');
                    }
                });
            });
    },

    editLink: function (i, a) {
        var $md = $('#' + DP.config.showMd);
        var $fm = $md.find('form');
        $fm[0].reset();

        var t = $(a).data('modalTitle');
        $md.find('.modal-title').html(t);

        DP.config.editBefore(i);
        $md.modal('show');
    },


    table: {
        option: {
            uniqueId: 'id',
            toolbar: '#toolbar',
            pagination: true,
            paginationLoop: false,
            sidePagination: 'server',
            method: 'post',
            contentType: "application/x-www-form-urlencoded",
            dataField: "data",
            queryParamsType: '',
            queryParams: function queryParams(params) {
                var param = {
                    pageNo: params.pageNumber,
                    pageSize: params.pageSize,
                };

                var $fm = $('#' + DP.config.searchFm);
                if ($fm){
                    serializeForm($fm, param);
                }

                return param;
            },
            formatLoadingMessage: function () {
                return "正在加载数据中，请稍候……"
            },
            formatShowingRows: function (a, b, c) {
                return "第 " + a + " / " + b + " 条，共 " + c + " 条 ";
            },
            formatRecordsPerPage: function (a) {
                return "每页 " + a;
            }
        },


        init: function (option) {
            if (typeof option != 'undefined') {
                for (var attr in option) {
                    this.option[attr] = option[attr];
                }
            }

            var $table = DP.getTable();
            $table.bootstrapTable(this.option);

            var $fm = $('#' + DP.config.searchFm);
            if ($fm){
                $fm.find('.search').on('click', function () {
                    $table.bootstrapTable("refresh");
                });
            }
        }
    },

    getTable: function () {
        return $('#' + DP.config.bsTable);
    }
};

function serializeForm(fm, param) {
    var array = fm.serializeArray();
    // 序列化表单内容
    $(array).each(function() {
        param[this.name] = this.value.trim();
    })
}