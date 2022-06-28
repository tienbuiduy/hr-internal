$(function () {
	var tableFields = [
		{
			name: 'Code',
			valueKey: 'employeeCode',
			canBeSort: true
		},
		{
			name: 'Employee Name',
			valueKey: 'employeeName',
			canBeSort: true
		},
		{
			name: 'Position',
			valueKey: 'roles',
			canBeSort: true
		},
		{
			name: 'Email',
			valueKey: 'email',
			canBeSort: true
		},
		{
			name: 'Note',
			valueKey: 'note',
			canBeSort: true,
			isTextArea: true
		},
		{
			name: 'Main Skill',
			valueKey: 'mainSkill',
			canBeSort: true,
			isTextArea: true
		},
		{
			name: 'Status',
			valueKey: 'statusName',
			canBeSort: true
		},
		{
			name: 'Group',
			valueKey: 'groupName',
			canBeSort: true,
			isTextArea: true
		},
		{
			name: 'Department',
			valueKey: 'department',
			canBeSort: true,
			isTextArea: true
		},
		{
			name: 'Working Day',
			valueKey: 'workingDay',
			canBeSort: true,
			isTextArea: true
		}
	];
	var $tableEmployee;
	var $formActionEmployee = $('.formActionEmployee');
	var $modalActionEmployee = $('#modalActionEmployee');

	/* prepare data */
	$(document).ready(function () {
		initEventHandleEmployee();
		initTableEmployee();
		initValidateFormActionEmployee();
		bulkFetchDataEmployee();
	});

	function initEventHandleEmployee() {
		$('#btnSearchEmployee').on('click', searchEmployee);
		$('#addEmployee').on('click', openFormCreateEmployee);

		$('.btnEditEmployee').on('click', onEditEmployee);
		$('.btnAddEmployee').on('click', onAddEmployee);
		$('#btnResetEmployee').on('click', onResetSearchEmployee);
		$('#employeeCode ').on('keypress', onEnterSearchEmployee);
		$('#employeeName').on('keypress', onEnterSearchEmployee);
		$('#mainSkill').on('keypress', onEnterSearchEmployee);
		$('#email').on('keypress', onEnterSearchEmployee);
		$('#groupName').on('keypress', onEnterSearchEmployee);
		$('#department').on('keypress', onEnterSearchEmployee);


	}
	function onEnterSearchEmployee(e) {
		if(e.which === 13){searchEmployee();}
	}
	function initTableEmployee() {
		$tableEmployee = $('#tableEmployee').dataTable({
			fields: tableFields,
			showDefaultAction: true,
			onEditItem: openFormEditEmployee,
			onDeleteItem: confirmDeleteCustomer,
			showColumnNo: true,
			pagingByClient: true,
			sortByClient: true,
			actions: [{

				icon: 'glyphicon-eye-open',
				funcAction: openFormViewEmployee
			}]
		});
	}

	function fetchListEmployee() {
		CommonUtils.getApi('/api/employees').then(function (data) {
			$tableEmployee.setItemsCore(transformDataTableEmployee(data));
		});
	}

	function bulkFetchDataEmployee() {
		CommonUtils.showLoading();
		$.when(
			CommonUtils.getApi('/api/employees'),
			CommonUtils.getApi('/api/employees/roles'),
			CommonUtils.getApi('/api/employees/status')
		).then(function (employees, roles, statuses) {
			CommonUtils.hideLoading();
			listRole = roles;
			listStatus = statuses;
			buildFormFieldRole($("#form-control__item--roles"));
			buildFormCheckboxFieldsStatus($("#form-control__item--status"));
			$tableEmployee.setItemsCore(transformDataTableEmployee(employees));
		})
	}

	function initValidateFormActionEmployee() {
		$formActionEmployee.validate({
			messages: {
				employeeName: {
					required: CommonMessage.getMessageRequired('Employee Name'),
				},
				email: {
					required: CommonMessage.getMessageRequired('Email')
				},
				role: {
					required: CommonMessage.getMessageRequired('Role')
				}
			}
		});
	}

	function onAddEmployee() {
		var employeeModel = CommonUtils.buildModelRequestBody($formActionEmployee.find('.form-control__item'));
		var $formValid = $formActionEmployee.valid();

		if (validateCustomEmployeeField(employeeModel) && $formValid) {
			CommonUtils.showLoading();

			CommonUtils.postApi('/api/employees', employeeModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionEmployee.modal('hide');
				$tableEmployee.setPage(1);
				fetchListEmployee();
				CommonModal.message({ message: 'Create employee successfully!' });
			}, function (error) {
				var messages = error.map(function (item) { return item.message; }).join(', ');
				CommonModal.message({message: messages, noAutoClose: true});
			});
		}
	}

	function onEditEmployee() {
		var employeeModel = CommonUtils.buildModelRequestBody($formActionEmployee.find('.form-control__item'));
		var $formValid = $formActionEmployee.valid();

		if (validateCustomEmployeeField(employeeModel) && $formValid) {
			CommonUtils.showLoading();
			employeeModel.id = $('.btnEditEmployee').data('id');

			CommonUtils.postApi('/api/employees', employeeModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionEmployee.modal('hide');
				fetchListEmployee();
				CommonModal.message({ message: 'Edit employee successfully!' });
			}, function (error) {
				var messages = error.map(function (item) { return item.message; }).join(', ');
				CommonModal.message({message: messages, noAutoClose: true});
			});
		}
	}

	function validateCustomEmployeeField(employeeModel) {
		var rolesValid = employeeModel.roles.length;
		var statusValid = employeeModel.status !== undefined && employeeModel.status !== null;

		$formActionEmployee.find('.form-control__roles-error').toggle(!rolesValid);
		$formActionEmployee.find('.form-control__status-error').toggle(!statusValid);

		return rolesValid && statusValid;
	}

	function listenerCustomEmployeeField() {
		var $rolesCheckboxes = $('.form-control__roles input[type="checkbox"]', $formActionEmployee);
		var $statusRadios = $('.form-control__status input[type="radio"]', $formActionEmployee);

		$rolesCheckboxes.on('change', onChangeValue);
		$statusRadios.on('change', onChangeValue);

		function onChangeValue() {
			var employeeModel = CommonUtils.buildModelRequestBody($formActionEmployee.find('.form-control__item'));
			validateCustomEmployeeField(employeeModel);
		}
	}

	function onDeleteEmployee(item) {
		CommonUtils.showLoading();
		CommonUtils.postApi('/api/employees/' + item.id, null, 'DELETE').then(function (res) {
			CommonUtils.hideLoading();
			$tableEmployee.handlePageWhenDeleteItem();
			fetchListEmployee();
			CommonModal.message({ message: 'Delete employee successfully!' });
		});
	}

	function openFormEditEmployee(employee) {
		$('.btnEditEmployee').show();
		$('.btnAddEmployee').hide();

		$('.modal-title', $modalActionEmployee).text('Edit Employee').prop("disabled", false);
		$('.field-employee__employee-code', $formActionEmployee).val(employee.employeeCode).prop("disabled", false);
		$('.field-employee__employee-name', $formActionEmployee).val(employee.employeeName).prop("disabled", false);
		$('.field-employee__email', $formActionEmployee).val(employee.email).prop("disabled", false);
		$('.field-employee__note', $formActionEmployee).val(employee.note).prop("disabled", false);
		$('.field-employee__main-skill', $formActionEmployee).val(employee.mainSkill).prop("disabled", false);
		$('.field-employee__status', $formActionEmployee).val(employee.statusName).prop("disabled", false);
		$('.field-employee__group', $formActionEmployee).val(employee.groupName).prop("disabled", false);
		$('.field-employee__department', $formActionEmployee).val(employee.department).prop("disabled", false);
		$('.field-employee__date', $formActionEmployee).val(employee.workingDay).prop("disabled", false);
		$('.btnEditEmployee').data('id', employee.id);
		handleDefaultFormActionAllocation(employee);

		$modalActionEmployee.modal('show');
	}

	function openFormViewEmployee(employee) {
		handleOpenViewEmployee(employee)
	}

	function openFormCreateEmployee() {
		$('.btnEditEmployee').hide();
		$('.btnAddEmployee').show();
		$('.modal-title', $modalActionEmployee).text('Create Employee');
		$formActionEmployee.find('.form-control').val('');
		$formActionEmployee.find('.form-control__roles .form-control__item, .form-control__status .form-control__item').prop('checked', false);
		handleDefaultFormActionAllocation();

		CommonUtils.getApi('/api/employees/generateCode').then(function (employeeCode) {
			$formActionEmployee.find('.field-employee__employee-code').val(employeeCode);
			$modalActionEmployee.modal('show');
		});
	}

	function handleDefaultFormActionAllocation(employee) {
		var roles = employee && employee.roles && employee.roles.split(', ') || [];
		var status = employee && employee.status;

		$formActionEmployee.validate().resetForm();
		$formActionEmployee.find('.form-control__item').removeClass('error');
		$formActionEmployee.find('.form-control__roles, .form-control__status').empty();
		$formActionEmployee.find('.form-control__roles-error, .form-control__status-error').hide();
		buildFormFieldRole($('.form-control__roles'), roles);
		buildFormRadioFieldsStatus($('.form-control__status'), [status], (employee && employee.employeeCode || '') + 'EmployeeStatus');
		// listenerCustomEmployeeField();
	}
	function handleDefaultFormActionAllocation1(employee) {
		var roles = employee && employee.roles && employee.roles.split(', ') || [];
		var status = employee && employee.status;

		$formActionEmployee.validate().resetForm();
		$formActionEmployee.find('.form-control__item').removeClass('error');
		$formActionEmployee.find('.form-control__roles, .form-control__status').empty();
		$formActionEmployee.find('.form-control__roles-error, .form-control__status-error').hide();
		buildFormFieldRole1($('.form-control__roles'), roles);
		buildFormRadioFieldsStatus1($('.form-control__status'), [status], (employee && employee.employeeCode || '') + 'EmployeeStatus');
		// listenerCustomEmployeeField();
	}
	function confirmDeleteCustomer(item) {
		CommonModal.confirm({
			title: 'Delete Employee',
			message: 'Are you sure to delete this employee?',
			confirmed: onDeleteEmployee.bind(this, item)
		});
	}

	function transformDataTableEmployee(data) {
		data = data.map(function (item) {
			if (item.roles) {
				item.roles = item.roles.map(function (role) {
					return role.roleName;
				}).join(', ');
			}
			item.workingDay = CommonUtils.formatDateToString(item.workingDay);
			return item;
		});

		return data;
	}

	function buildFormFieldRole($selector, listChecked) {
		buildFormCheckFields($selector, listChecked, listRole, {
			field: 'roles',
			label: 'roleName',
			name: 'role',
		});
	}
	function buildFormFieldRole1($selector, listChecked) {
		buildFormCheckFields1($selector, listChecked, listRole, {
			field: 'roles',
			label: 'roleName',
			name: 'role',
		});
	}
	function buildFormCheckboxFieldsStatus($selector, listChecked, inputName) {
		buildFormCheckFields($selector, listChecked, listStatus, {
			field: 'status',
			name: inputName || 'status',
			value: 'code',
			type: 'string',
			disabled: true
		});
	}

	function buildFormRadioFieldsStatus($selector, listChecked, inputName) {
		buildFormCheckFields($selector, listChecked, listStatus, {
			inputType: 'radio',
			field: 'status',
			name: inputName || 'status',
			value: 'code',
			type: 'string',
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
	function buildFormCheckFields($selector, listChecked, list, options) {
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
					'checked '+
					'value="' + item[options.value] + '"/>' + item[options.label]
				);
			}
			else{
				$label.append('<input type="' + options.inputType + '"' +
					'name="' + options.name + '"' +
					'data-field="' + options.field + '"' +
					'data-type="' + options.type + '"' +
					'class="form-control__item"'
					+'value="' + item[options.value] + '"/>' + item[options.label]);
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
	function searchEmployee() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildModelRequestBody($('.form-search-employee').find('.form-control__item'));

		CommonUtils.postApi('/api/employees/search', searchModal, 'POST').then(function (employees) {
			CommonUtils.hideLoading();
			$tableEmployee.setPage(1);
			$tableEmployee.setItemsCore(transformDataTableEmployee(employees));
		});
	}

	function onResetSearchEmployee() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildEmptyModelRequestBodyEmployees($('.form-search-employee').find('.form-control__item'));

		$("input[name='status']").each(function () {
			if($(this).val() == 1){
				$(this).prop( "checked", true );
			}else
			{
				$(this).prop( "checked", false );
			}
		});
		$("input[name='role']").each(function () {
			$(this).prop( "checked", false );
		});
		$('.field-search').val("");

		CommonUtils.postApi('/api/employees/search', searchModal, 'POST').then(function (employees) {
			CommonUtils.hideLoading();
			$tableEmployee.setPage(1);
			$tableEmployee.setItemsCore(transformDataTableEmployee(employees));
		});
	}
});
