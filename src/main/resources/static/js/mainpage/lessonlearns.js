$(function () {
    var tableFieldsLessonlearn = [
        {
            name: 'Date',
            valueKey: 'date',
            canBeSort: true
        },
        {
            name: 'Description',
            valueKey: 'description',
            canBeSort: true
        },
        {
            name: 'Category',
            valueKey: 'category',
            canBeSort: true
        },
        {
            name: 'Impact',
            valueKey: 'impact',
            canBeSort: true,
            isTextArea: true
        },
        {
            name: 'Root Cause',
            valueKey: 'rootCause',
            canBeSort: true,
            isTextArea: true
        },
        {
            name: 'Corrective Action',
            valueKey: 'correctiveAction',
            canBeSort: true,
            isTextArea: true
        },
        {
            name: 'PIC',
            valueKey: 'pic',
            canBeSort: true
        },
        {
            name: 'Deadline',
            valueKey: 'deadLine',
            canBeSort: true
        },
        {
            name: 'Expected Result',
            valueKey: 'expectResult',
            canBeSort: true
        },
        {
            name: 'Actual Result',
            valueKey: 'actualResult',
            canBeSort: true
        },
        {
            name: 'Status',
            valueKey: 'status',
            canBeSort: true
        },
        {
            name: 'PMO Opinion',
            valueKey: 'opinion',
            canBeSort: true
        },
        {
            name: 'Other Notes',
            valueKey: 'otherNotes',
            canBeSort: true
        },
        {
            name: 'Problem Source',
            valueKey: 'problemSource',
            canBeSort: true
        },
        {
            name: 'Long Term Solution',
            valueKey: 'longTermSolution',
            canBeSort: true
        }
    ];
    var $tableLessonlearn;

    /* prepare data */
    $(document).ready(function () {
        initEventHandleLessonlearn();
        initTableLessonlearn();
        bulkFetchDataLessonlearn();
    });

    function initEventHandleLessonlearn() {
        $('#btnSearchLessonlearn').on('click', searchLessonlearn);
        $('#btnResetsearchLessonlearn').on('click', onResetSearchLessonlearn);
        $('#btnImportLessonlearn').on('click', onImportLessonlearn);
    }

    function initTableLessonlearn() {
        $tableLessonlearn = $('#tableLessonlearn').dataTable({
            fields: tableFieldsLessonlearn,
            showDefaultAction: true,
            onDeleteItem: confirmDeleteLessonlearn,
            showColumnNo: true,
            pagingByClient: true,
            sortByClient: true
        });
    }

    function fetchListLessonlearn() {
        CommonUtils.getApi('/api/lessonlearns').then(function (data) {
            $tableLessonlearn.setItemsCore(transformDataTableLessonlearn(data));
        });
    }

    function onDeleteLessonlearn(item) {
        CommonUtils.showLoading();
        CommonUtils.postApi('/api/lessonlearns/' + item.id, null, 'DELETE').then(function (res) {
            CommonUtils.hideLoading();
            fetchListLessonlearn();
            CommonModal.message({ message: 'Delete lesson learn successfully!' });
        });
    }



    function confirmDeleteLessonlearn(item) {
        CommonModal.confirm({
            title: 'Delete Employee',
            message: 'Are you sure to delete this lesson learn?',
            confirmed: onDeleteLessonlearn.bind(this, item)
        });
    }

    function transformDataTableLessonlearn(data) {
        return data.map(function (item) {
            item.date = CommonUtils.formatDateToString(item.date);
            item.deadLine = CommonUtils.formatDateToString(item.deadLine);
            return item;
        });
    }

    function searchLessonlearn() {
        CommonUtils.showLoading();
        var searchModal = CommonUtils.buildModelRequestBody($('.form-search-lessonlearns').find('.form-control__item'));

        CommonUtils.postApi('/api/lessonlearns/search', searchModal, 'POST').then(function (employees) {
            CommonUtils.hideLoading();
            $tableLessonlearn.setPage(1);
            $tableLessonlearn.setItemsCore(transformDataTableLessonlearn(employees));
        });
    }


    function onResetSearchLessonlearn() {
        CommonUtils.showLoading();
        var searchModal = CommonUtils.buildEmptyModelRequestBodyLessonlearns($('.form-search-lessonlearn').find('.form-control__item'));
        $("input[name='role']").each(function () {
            $(this).prop( "checked", false );
        });

        $('.field-search').val("");
        CommonUtils.postApi('/api/lessonlearns/search', searchModal, 'POST').then(function (lessonlearns) {
            CommonUtils.hideLoading();
            $tableLessonlearn.setPage(1);
            $tableLessonlearn.setItemsCore(transformDataTableLessonlearn(lessonlearns));
        });
    }

    function onImportLessonlearn() {
        var modal = document.getElementById("import-lessonlearns");

        var btn = document.getElementById("btnImportLessonlearn");
        btn.onclick = function() {
            modal.style.display = "block";
        }
    }

    $(document).ready(function () {
        $("#import-lessonlearn").change(function (e) {
            e.preventDefault();
            var file_data = $('#import-lessonlearn').prop('files')[0];
            var form_data = new FormData();
            form_data.append('file', file_data);
            $.ajax({
                url: "/api/lessonlearns/file-upload",
                type: "POST",
                data: form_data,
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false,
                cache: false,
                success: function (res) {
                    CommonModal.message({message: res, noAutoClose: true});
                },
                error: function (err) {
                    CommonModal.message({message: err, noAutoClose: true});
                }
            });
        });
    });

    function bulkFetchDataLessonlearn() {
        CommonUtils.showLoading();
        $.when(
            CommonUtils.getApi('/api/lessonlearns')
        ).then(function (lessonlearns) {
            CommonUtils.hideLoading();
            $tableLessonlearn.setItemsCore(transformDataTableLessonlearn(lessonlearns));
        })
    }

    $(document).ready(function () {
        $("#import-lessonlearns").change(function (e) {
            e.preventDefault();
            var file_data = $('#import-lessonlearns').prop('files')[0];
            var form_data = new FormData();
            form_data.append('file', file_data);
            $.ajax({
                url: "/api/lessonlearns/import",
                type: "POST",
                data: form_data,
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false,
                cache: false,
                success: function (res) {
                    CommonModal.message({message: res, noAutoClose: true});
                    bulkFetchDataLessonlearn();
                },
                error: function (err) {
                    CommonModal.message({message: err.responseText, noAutoClose: true});
                    bulkFetchDataLessonlearn();
                }
            });
        });
    });
});