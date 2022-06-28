$(function () {

    var customerAppParams = [];
    var listCustomer = [];
    var $tableCustomer;
    var tableFields = [
        {
            name: 'Code',
            valueKey: 'customerCode',
            canBeSort: true,
        },
        {
            name: 'Customer Name',
            valueKey: 'customerName',
            canBeSort: true
        },
        {
            name: 'Country',
            valueKey: 'countryCode',
            canBeSort: true
        },
        {
            name: 'Rank',
            valueKey: 'rank',
            canBeSort: true
        },
        {
            name: 'Experience',
            valueKey: 'experience',
            canBeSort: true,
            isTextArea: true
        }
    ];
    var $formActionCustomer = $('.formActionCustomer');
    var $modalActionCustomer = $('#modalActionCustomer');

    /* prepare data */
    $(document).ready(function () {
        initEventHandleCustomer();
        initTableAllocation();
        initValidateFormActionCustomer();
        bulkFetchDataCustomer();
    });

    function initValidateFormActionCustomer() {
        $formActionCustomer.validate({
            messages: {
                customerName: {
                    required: CommonMessage.getMessageRequired('Customer Name')
                },
                countryCode: {
                    required: CommonMessage.getMessageRequired('Country')
                },
                rank: {
                    required: CommonMessage.getMessageRequired('Rank')
                }
            }
        });
    }

    function initTableAllocation() {
        $tableCustomer = $('#tableCustomer').dataTable({
            fields: tableFields,
            showDefaultAction: true,
            onEditItem: openFormEditCustomer,
            onDeleteItem: confirmDeleteCustomer,
            showColumnNo: true,
            pagingByClient: true,
            sortByClient: true,
        });
    }

    function initEventHandleCustomer() {
        $('#btnSearchCustomer').on('click', searchCustomer);
        $('#addCustomer').on('click', openFormCreateCustomer);

        $('.btnAddCustomer').on('click', onAddCustomer);
        $('.btnEditCustomer').on('click', onEditCustomer);
        $('#btnResetCustomer').on('click', onResetSearchCustomer);
        $('#customerCode').on('keypress', onEnterSearchCustomer);
        $('#customerName').on('keypress', onEnterSearchCustomer);
    }

    function onEnterSearchCustomer(e) {
        if(e.which === 13){searchCustomer();}
    }
    function fetchListCustomer() {
        CommonUtils.getApi('/api/customers').then(function (customers) {
            $tableCustomer.setItemsCore(transformDataTableCustomer(customers));
        });
    }

    function bulkFetchDataCustomer() {
        CommonUtils.showLoading();
        $.when(
            CommonUtils.getApi('/api/customers'),
            CommonUtils.getApi('/api/customers/appParams')
        ).then(function (customers, appParams) {
            CommonUtils.hideLoading();
            customerAppParams = appParams;
            buildFormCountryCustomer();
            $tableCustomer.setItemsCore(transformDataTableCustomer(customers));
        })
    }

    function openFormCreateCustomer() {
        $('.btnAddCustomer').show();
        $('.btnEditCustomer').hide();
        $('.modal-title', $modalActionCustomer).text('Create Customer');
        handleDefaultFormActionCustomer();
        $modalActionCustomer.find('.form-control__item').val('');

        CommonUtils.getApi('/api/customers/generateCode').then(function (customerCode) {
            $modalActionCustomer.find('.field-customer__customer-code').val(customerCode);
            $modalActionCustomer.modal('show');
        });
    }

    function openFormEditCustomer(item) {
        $('.btnAddCustomer').hide();
        $('.btnEditCustomer').show();
        $('.btnEditCustomer').data('id', item.id);
        $('.modal-title', $modalActionCustomer).text('Edit Customer');
        handleDefaultFormActionCustomer();

        $modalActionCustomer.find('.field-customer__customer-code').val(item.customerCode);
        $modalActionCustomer.find('.field-customer__customer-name').val(item.customerName);
        $modalActionCustomer.find('.field-customer__country-code').val(item.countryId);
        $modalActionCustomer.find('.field-customer__rank').val(item.rankId);
        $modalActionCustomer.find('.field-customer__experience').val(item.experience);
        $modalActionCustomer.modal('show');
    }

    function handleDefaultFormActionCustomer() {
        var options = generateOptions();

        $formActionCustomer.validate().resetForm();
        $formActionCustomer.find('.form-control__item').removeClass('error');
        $('.field-customer__country-code').empty();
        $('.field-customer__rank').empty();
        $('.field-customer__country-code').append(options.countryCodeHtml);
        $('.field-customer__rank').append(options.countryRankHtml);
    }

    function confirmDeleteCustomer(item) {
        CommonModal.confirm({
            title: 'Delete Customer',
            message: 'Are you sure to delete this customer?',
            confirmed: onDeleteCustomer.bind(this, item)
        });
    }

    function onAddCustomer() {
        if ($formActionCustomer.valid()) {
            var customerModel = CommonUtils.buildModelRequestBody($formActionCustomer.find('.form-control__item'));
            CommonUtils.showLoading();

            CommonUtils.postApi('/api/customers', customerModel, 'POST').then(function (res) {
                CommonUtils.hideLoading();
                $modalActionCustomer.modal('hide');
                $tableCustomer.setPage(1);
                fetchListCustomer();
                CommonModal.message({ message: 'Create customer successfully!' });
            });
        }
    }

    function onEditCustomer() {
        if ($formActionCustomer.valid()) {
            var customerModel = CommonUtils.buildModelRequestBody($formActionCustomer.find('.form-control__item'));
            customerModel.id = $('.btnEditCustomer').data('id');
            CommonUtils.showLoading();

            CommonUtils.postApi('/api/customers', customerModel, 'POST').then(function (res) {
                CommonUtils.hideLoading();
                $modalActionCustomer.modal('hide');
                fetchListCustomer();
                CommonModal.message({ message: 'Edit customer successfully!' });
            });
        }
    }

    function onDeleteCustomer(item) {
        CommonUtils.showLoading();
        CommonUtils.postApi('/api/customers/' + item.id, null, 'DELETE').then(function (res) {
            CommonUtils.hideLoading();
            $tableCustomer.handlePageWhenDeleteItem();
            fetchListCustomer();
            CommonModal.message({ message: 'Delete customer successfully!' });
        });
    }

    function transformDataTableCustomer(customers) {
        customers = customers.map(function (customer) {
            customerAppParams.forEach(function (customerAppParam) {
                if (customerAppParam.type === 'COUNTRY_TYPE' && customerAppParam.code === customer.countryCode) {
                    customer.countryCode = customerAppParam.name;
                    customer.countryId = customerAppParam.code;
                }
                if (customerAppParam.type === 'CUSTOMER_RANK_TYPE' && customerAppParam.code === customer.rank) {
                    customer.rank = customerAppParam.name;
                    customer.rankId = customerAppParam.code;
                }
            });

            return customer;
        });

        return customers;
    }

    function buildFormCountryCustomer() {
        var options = generateOptions(true);

        $('.form-search-control__country-code').empty();
        $('.form-search-control__rank').empty();
        $('.form-search-control__country-code').append(options.countryCodeHtml);
        $('.form-search-control__rank').append(options.countryRankHtml);
    }

    function generateOptions(optionEmpty) {
        var countryCodeHtml = '';
        var countryRankHtml = '';

        countryCodeHtml += '<option value="">-- Select --</option>';
        countryRankHtml += '<option value="">-- Select --</option>';

        customerAppParams.forEach(function (item) {
            if (item.type === 'COUNTRY_TYPE') {
                countryCodeHtml += ('<option data-name="' + item.name + '" value=' + item.code + '>' + item.name + '</option>')
            }

            if (item.type === 'CUSTOMER_RANK_TYPE') {
                countryRankHtml += ('<option data-name="' + item.name + '" value=' + item.code + '>' + item.name + '</option>')
            }
        });

        return {
            countryCodeHtml: countryCodeHtml,
            countryRankHtml: countryRankHtml
        }
    }

    function searchCustomer() {
        CommonUtils.showLoading();
        var searchModal = CommonUtils.buildModelRequestBody($('.form-search-customer').find('.form-control__item'));

        CommonUtils.postApi('/api/customers/search', searchModal, 'POST').then(function (customers) {
            CommonUtils.hideLoading();
            $tableCustomer.setPage(1);
            $tableCustomer.setItemsCore(transformDataTableCustomer(customers));
        });
    }

    function onResetSearchCustomer() {
        CommonUtils.showLoading();
        var searchModal = CommonUtils.buildEmptyModelRequestBodyCustomers($('.form-search-customer').find('.form-control__item'));

        $('.field-search').val("");
        $('.form-select').prop('selectedIndex',0);


        CommonUtils.postApi('/api/customers/search', searchModal, 'POST').then(function (customers) {
            CommonUtils.hideLoading();
            $tableCustomer.setPage(1);
            $tableCustomer.setItemsCore(transformDataTableCustomer(customers));
        });
    }
});
