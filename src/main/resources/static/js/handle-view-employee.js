function handleOpenViewEmployee(employee) {
    var $modalActionEmployee = $('#modalActionEmployee');
    var $formActionEmployee = $('.formActionEmployee');

    $('.btnEditEmployee').hide();
    $('.btnAddEmployee').hide();
    $('.modal-title', $modalActionEmployee).text('View Employee');
    $('.field-employee__employee-code', $formActionEmployee).val(employee.employeeCode).prop("disabled", true);
    $('.field-employee__employee-name', $formActionEmployee).val(employee.employeeName).prop("disabled", true);
    $('.field-employee__email', $formActionEmployee).val(employee.email).prop("disabled", true);
    $('.field-employee__note', $formActionEmployee).val(employee.note).prop("disabled", true);
    $('.field-employee__main-skill', $formActionEmployee).val(employee.mainSkill).prop("disabled", true);
    // $('.field-employee__status', $formActionEmployee).val(employee.statusName).prop("disabled", true);
    $('.field-employee__group', $formActionEmployee).val(employee.groupName).prop("disabled", true);
    $('.field-employee__department', $formActionEmployee).val(employee.department).prop("disabled", true);
    $('.field-employee__date', $formActionEmployee).val(employee.workingDay).prop("disabled", true);
    $('.form-control__roles', $formActionEmployee).val(employee.workingDay).prop("disabled", true);
    // $('.btnEditEmployee').data('id', employee.id);
    handleDefaultFormActionAllocation1(employee,$formActionEmployee);

    $modalActionEmployee.modal('show');
}
function handleDefaultFormActionAllocation1(employee, $formActionEmployee) {
    var roles = employee && employee.roles || [];
    var status = employee && employee.status;

    $formActionEmployee.validate().resetForm();
    $formActionEmployee.find('.form-control__item').removeClass('error');
    $formActionEmployee.find('.form-control__roles, .form-control__status').empty();
    $formActionEmployee.find('.form-control__roles-error, .form-control__status-error').hide();
    buildFormFieldRole1($('.form-control__roles'), roles);
    buildFormRadioFieldsStatus1($('.form-control__status'), [status], (employee && employee.employeeCode || '') + 'EmployeeStatus');
    // listenerCustomEmployeeField();
}
function buildFormFieldRole1($selector, listChecked) {
    buildFormCheckFields1($selector, listChecked, listRole, {
        field: 'roles',
        label: 'roleName',
        name: 'role',
    });
}
function buildFormRadioFieldsStatus1($selector, listChecked, inputName) {
    buildFormCheckFields1($selector, listChecked, listStatus, {
        inputType: 'radio',
        field: 'status',
        name: inputName || 'status',
        value: 'code',
        type: 'string',
    });
}
function buildFormCheckFields1($selector, listChecked, list, options) {
    if (options.type === 'string') {
        listChecked = (listChecked || []).map(function (val) {
            return (val || '').toString();
        });
    }
    options = $.extend({
        value: 'id',
        label: 'name',
        type: 'number',
        field: '',
        name: '',
        inputType: 'checkbox',
    }, options);

    list.forEach(function (item) {
        var $div = $('<div></div>').addClass(options.inputType + ' form-search-' + options.inputType);
        var $label = $('<label></label>');
        if(item.code === '1'){
            $label.append('<input type="' + options.inputType + '"' +
                'name="' + options.name + '"' +
                'data-field="' + options.field + '"' +
                'data-type="' + options.type + '"' +
                'class="form-control__item"' +
                'checked '+'disabled="true"'+
                'value="' + item[options.value] + '"/>' + item[options.label]
            );
        }
        else{
            $label.append('<input type="' + options.inputType + '"' +
                'name="' + options.name + '"' +
                'data-field="' + options.field + '"' +
                'data-type="' + options.type + '"' +
                'class="form-control__item"'
                +'disabled="true"'+'value="' + item[options.value] + '"/>' + item[options.label]);
        }


        var $input = $label.find('input');

        if (listChecked && (options.inputType === 'radio' ?
            ~listChecked.indexOf(item[options.value]) :
            ~listChecked.indexOf(item[options.label]))) {
            $input.prop('checked', true);
        }

        $selector.append($div.append($label));
    });
}