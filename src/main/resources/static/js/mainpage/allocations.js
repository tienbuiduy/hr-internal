$(function () {
	var tableFieldsAllocation = [
		{
			name: 'Employee',
			valueKey: 'employeeName',
			canBeSort: true
		},
		{
			name: 'Position',
			valueKey: 'roleName',
			canBeSort: true
		},
		{
			name: 'Type',
			valueKey: 'type',
			canBeSort: true
		},
		{
			name: 'Rate',
			valueKey: 'rate',
			canBeSort: true
		},
		{
			name: 'Main Skill',
			valueKey: 'mainSkill',
			canBeSort: true,
			isTextArea: true
		},
		{
			name: 'Project',
			valueKey: 'projectNameDisplay',
			canBeSort: true
		},
		{
			name: 'Allocation',
			valueKey: 'allo',
			canBeSort: true
		},
		{
			name: 'PM',
			valueKey: 'pmNameDisplay',
			canBeSort: true
		},
		{
			name: 'Start Date',
			valueKey: 'startDate',
			canBeSort: true
		},
		{
			name: 'End Date',
			valueKey: 'endDate',
			canBeSort: true
		},
		{
			name: 'Note',
			valueKey: 'note',
			canBeSort: true
		},
		{
			name: 'Source',
			valueKey: 'source',
			canBeSort: true
		}
	];
	var tableFieldsAllocationDetails = [
		{
			name: 'Employee',
			valueKey: 'employeeName',
			canBeSort: true
		},
		{
			name: 'Position',
			valueKey: 'roleName',
			canBeSort: true
		},
		{
			name: 'Main Skill',
			valueKey: 'mainSkill',
			canBeSort: true,
			isTextArea: true
		},
		{
			name: 'Project',
			valueKey: 'projectNameDisplay',
			canBeSort: true
		},
		{
			name: 'PM',
			valueKey: 'pmNameDisplay',
			canBeSort: true
		}
	];
	var $tableAllocation;
	var $formActionAllocation = $('.formActionAllocation');
	var $modalActionAllocation = $('#modalActionAllocation');
	var $formExportEE = $('.form-export-ee');
	var $formImport=$('.form-import');
	var $modalExportEE = $('#modal-export-ee');
	var $modalImport=$('#modalImport');
	var $modalViewAllocationDetail = $('#modalViewAllocationDetail');
	var $tableAllocationDetails;
	var roles = [];
	var projects = [];
	var projectsOfLoginUser = [];
	var opportunities = [];
	var employees = [];
	var allocationDetails = [];
	var allEmployees = [];
	var types = [];
	var $modalExportAllocation = $('#modalExportAllocation');

	/* prepare data */
	$(document).ready(function () {
		initEventHandleAllocation();
		initTableAllocation();
		initValidateFormActionAllocation();
		bulkFetchDataAllocation();
	});

	function initEventHandleAllocation() {
		$('#btnSearchAllocation').on('click', searchAllocation);
		$('#addAllocation').on('click', openFormCreateAllocation);
		$('#btnViewAllocation').on('click', openModalViewAllocation);
		$('#btnViewAllocationDetail').on('click', viewAllocation);
		$('.field-allocation__employee', $formActionAllocation).on('change', onChangeEmployee);
		$('#btnExportAllocation').on('click', openFormExportAllocation);

		$('.btnEditAllocation').on('click', onEditAllocation);
		$('.btnAddAllocation').on('click', onAddAllocation);
		$('.field-allocation__end-date, .field-allocation__start-date').on('change', validateRangeDate);
		$('.field-allocation__employee, .field-allocation__project, .form-search-user, .form-search-project').combobox();
		$('#btnResetSearchAllocation').on('click', onResetSearchAllocation);

		$('#btnViewExportEE').on('click', openFormExportEE);
		$('#btnViewImport').on('click', openFormImport);
		$('.btnExportEE').on('click', onExportEE);
		$('#btnExportAllocationByDay').on('click', onExportAllocationByDay);
		$('#btnExportAllocationByMonth').on('click', onExportAllocationByMonth);
		$('#btnExportAllocationByPeriod').on('click', onExportAllocationByPeriod);
		$('#fromStartDate').on('keyup', onEnterSearchAllocation);
		$('#toStartDate').on('keyup', onEnterSearchAllocation);
		$('#fromEndDate').on('keyup', onEnterSearchAllocation);
		$('#toEndDate').on('keyup', onEnterSearchAllocation);
		$('#projectName').on('keypress', onEnterSearchAllocation);
		$('#employeeName').on('keypress', onEnterSearchAllocation);
		$('#pmName').on('keypress', onEnterSearchAllocation);
		$('#startRate').on('keypress', onEnterSearchAllocation);
		$('#endRate').on('keypress', onEnterSearchAllocation);
	}
	function onEnterSearchAllocation(e) {
		var p=e.which;
		if(p == 13){searchAllocation();}
	}
	function initTableAllocation() {
		$tableAllocation = $('#tableAllocation').dataTable({
			fields: tableFieldsAllocation,
			showDefaultAction: true,
			onEditItem: openFormEditAllocation,
			onDeleteItem: confirmDeleteAllocation,
			getRowColor: getRowColor,
			showColumnNo: true,
			pagingByClient: true,
			sortByClient: true
		});

		$tableAllocationDetails = $('#tableAllocationDetails').dataTable({
			fields: tableFieldsAllocationDetails,
			getValueForCell: getValueForCell,
			showColumnNo: true,
			pagingByClient: true,
			sortByClient: true,
			handleChangeItem: handleChangeItem,
			getRowColor: getRowColor,
			fixedColumns: {
				leftColumns: 3,
			}
		});
	}

	function getRowColor(rowItem) {
		if(rowItem.status === '0') {
			return 'red';
		}
		return '#000';
	}
	function fetchListAllocation() {
		CommonUtils.getApi('/api/allocations').then(function (data) {
			$tableAllocation.setItemsCore(transformDataTableAllocation(data));
		});
	}

	function bulkFetchDataAllocation() {
		$.when(
			CommonUtils.getApi('/api/allocations'),
			CommonUtils.getApi('/api/employees/roles'),
			CommonUtils.getApi('/api/employees/listAllContainResignedEmployee'),
			CommonUtils.getApi('/api/projects'),
			CommonUtils.getApi('/api/projects/projectsOfLoginUser'),
			CommonUtils.getApi('/api/employees/listAllContainResignedEmployee'),
			CommonUtils.getApi('/api/opportunities'),
			CommonUtils.getApi('/api/allocations/type')
		).then(function (allocations, listRole, listEmployee, listProject, listProjectOfLoginUser, listAllContainResignedEmployee, listOpportunity, listType) {
			roles = listRole;
			employees = listEmployee;
			projects = listProject;
			projectsOfLoginUser = listProjectOfLoginUser;
			allEmployees = listAllContainResignedEmployee;
			opportunities = listOpportunity;
			types = listType;
			buildFormFieldRole($(".form-control__item--roles"));
			buildUserField($("#employeeNamePopup"));
			buildProjectField($("#projectNamePopup"));
			buildUserField($("#pmNamePopup"));
			buildFormCheckboxFieldsType($(".form-control__item--types"));
			$tableAllocation.setItemsCore(transformDataTableAllocation(allocations));
		})
	}

	function initValidateFormActionAllocation() {
		$formActionAllocation.validate({
			messages: {
				employeeId: {
					required: CommonMessage.getMessageRequired('Employee')
				},
				roleId: {
					required: CommonMessage.getMessageRequired('Role')
				},
				allo: {
					required: CommonMessage.getMessageRequired('Allocation')
				},
				rate: {
					required: CommonMessage.getMessageRequired('Rate')
				},
				startDate: {
					required: CommonMessage.getMessageRequired('Start Date')
				},
				endDate: {
					required: CommonMessage.getMessageRequired('End Date')
				}
			}
		});
	}

	function onAddAllocation() {
		var allocationModel = CommonUtils.buildModelRequestBody($formActionAllocation.find('.form-control__item'));
		var formValid = $formActionAllocation.valid();

		if (formValid && validateRangeDate()) {
			CommonUtils.showLoading();

			CommonUtils.postApi('/api/allocations', allocationModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionAllocation.modal('hide');
				$tableAllocation.setPage(1);
				fetchListAllocation();
				CommonModal.message({ message: 'Create allocation successfully!' });
			}, function (error) {
				var messages = error.map(function (item) { return item.message; }).join(', ');
				CommonModal.message({message: messages, noAutoClose: true});
			});
		}
	}

	function onEditAllocation() {
		var allocationModel = CommonUtils.buildModelRequestBody($formActionAllocation.find('.form-control__item'));
		var formValid = $formActionAllocation.valid();

		if (formValid && validateRangeDate()) {
			CommonUtils.showLoading();
			allocationModel.id = $('.btnEditAllocation').data('id');

			CommonUtils.postApi('/api/allocations', allocationModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionAllocation.modal('hide');
				fetchListAllocation();
				CommonModal.message({ message: 'Edit allocation successfully!' });
			}, function (error) {
				var messages = error.map(function (item) { return item.message; }).join(', ');
				CommonModal.message({message: messages, noAutoClose: true});
			});
		}
	}

	function validateRangeDate() {
		var startDate = CommonUtils.formatStringToDate($('.field-allocation__start-date', $formActionAllocation).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var endDate = CommonUtils.formatStringToDate($('.field-allocation__end-date', $formActionAllocation).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var validDate = startDate && endDate && startDate.getTime() < endDate.getTime();

		if (startDate && endDate) {
			$('#endDate-error-minDate').toggle(!validDate);
			$('.field-allocation__start-date').toggleClass('error', !validDate);
		}

		return validDate;
	}

	function validateExportEERangeDate() {
		var startDate = CommonUtils.formatStringToDate($('.field-export-ee__start-date', $formExportEE).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var endDate = CommonUtils.formatStringToDate($('.field-export-ee__end-date', $formExportEE).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var validDate = startDate && endDate && startDate.getTime() < endDate.getTime();

		if (startDate && endDate) {
			$('#ee-endDate-error-minDate').toggle(!validDate);
			$('.field-export-ee__start-date').toggleClass('error', !validDate);
		}

		if(startDate === null && endDate === null){
			return true;
		}
		return validDate;
	}

	function onDeleteAllocation(item) {
		CommonUtils.showLoading();
		CommonUtils.postApi('/api/allocations/' + item.id, null, 'DELETE').then(function (res) {
			CommonUtils.hideLoading();
			fetchListAllocation();
			CommonModal.message({ message: 'Delete allocation successfully!' });
		});
	}

	function openFormEditAllocation(allocation) {
		$('.btnEditAllocation').show();
		$('.btnAddAllocation').hide();
		handleDefaultFormActionAllocation();

		$('.modal-title', $modalActionAllocation).text('Edit Allocation');
		$('.field-allocation__project', $formActionAllocation).val(allocation.projectId).trigger('change');
		$('.field-allocation__opportunity', $formActionAllocation).val(allocation.oppId).trigger('change');
		$('.field-allocation__employee', $formActionAllocation).val(allocation.employeeId).trigger('change');
		$('.field-allocation__role', $formActionAllocation).val(allocation.roleId).trigger('change');
		$('.field-allocation__allo', $formActionAllocation).val(allocation.allo).trigger('change');
		$('.field-allocation__rate', $formActionAllocation).val(allocation.rate).trigger('change');
		$('.field-allocation__start-date', $formActionAllocation).val(allocation.startDate).trigger('change');
		$('.field-allocation__end-date', $formActionAllocation).val(allocation.endDate).trigger('change');
		$('.field-allocation__note', $formActionAllocation).val(allocation.note).trigger('change');
		$('.field-allocation__source', $formActionAllocation).val(allocation.source).trigger('change');
		$('.field-allocation__type', $formActionAllocation).val(allocation.type).trigger('change');
		$('.btnEditAllocation').data('id', allocation.id);

		$modalActionAllocation.modal('show');
	}

	function openFormCreateAllocation() {
		$('.btnEditAllocation').hide();
		$('.btnAddAllocation').show();
		$('.modal-title', $modalActionAllocation).text('Create Allocation');
		$formActionAllocation.find('.form-control').val('').trigger('change');
		handleDefaultFormActionAllocation();
		$modalActionAllocation.modal('show');
	}

	function openFormImport()
	{
		$('.modal-title',$modalImport).text('Import');
		$formImport.find('.form-control').val('').trigger('change');
		$modalImport.modal('show');
	}
	function openFormExportAllocation() {
		$('.modal-title', $modalExportAllocation).text('Export Allocation');
		$formActionAllocation.find('.form-control').val('').trigger('change');
		$modalExportAllocation.modal('show');
	}

	function openFormExportEE() {
		$('.modal-title', $modalExportEE).text('Export EE');
		$formActionAllocation.find('.form-control').val('').trigger('change');
		handleDefaultFormExportEE();
		$modalExportEE.modal('show');
	}

	function onExportEE() {
		var exportEEModal = CommonUtils.buildModelRequestBody($formExportEE.find('.form-control__item'));
		var projectId = $('.field-export-ee__item--select').val();

		if(projectId !== null && projectId !== '' && validateExportEERangeDate()){
			CommonUtils.postApi('/api/allocations/export-ee', exportEEModal, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				if (validateExportEERangeDate()) {
					window.open('/api/allocations/export-ee?', '_blank');
				}
			}, function (error) {
				var messages = error.map(function (item) { return item.message; }).join(', ');
				CommonModal.message({message: messages, noAutoClose: true});
			});
		}
	}

	function handleDefaultFormActionAllocation() {
		$formActionAllocation.validate().resetForm();
		buildFormActionAllocation();
		$('.form-control__item', $formActionAllocation).removeClass('error');
		$('#endDate-error-minDate', $formActionAllocation).hide();
	}

	function handleDefaultFormExportEE() {
		$formExportEE.validate().resetForm();
		buildFormExportEE();
		$('.form-control__item', $formExportEE).removeClass('error');
		$('#ee-endDate-error-minDate', $formExportEE).hide();
	}

	function confirmDeleteAllocation(item) {
		CommonModal.confirm({
			title: 'Delete Employee',
			message: 'Are you sure to delete this allocation?',
			confirmed: onDeleteAllocation.bind(this, item)
		});
	}

	function buildFormActionAllocation() {
		$('.field-allocation__item--select', $formActionAllocation).empty();
		var $project = $('.field-allocation__project', $formActionAllocation);
		var $opportunity = $('.field-allocation__opportunity', $formActionAllocation);
		var $employee = $('.field-allocation__employee', $formActionAllocation);
		var $role = $('.field-allocation__role', $formActionAllocation);
		var $type = $('.field-allocation__type', $formActionAllocation);

		$project.append('<option value="">-- Select --</option>');
		$opportunity.append('<option value="">-- Select --</option>');
		$employee.append('<option value="">-- Select --</option>');
		$role.append('<option value="">-- Select --</option>');
		$type.append('<option value="">-- Select --</option>');

		projects.forEach(function (project) {
			$project.append('<option value="' + project.id + '">' + project.projectName + '</option>');
		});
		opportunities.forEach(function (opportunity) {
			$opportunity.append('<option value="' + opportunity.id + '">' + opportunity.oppName + '</option>');
		});
		employees.forEach(function (employee) {
			$employee.append('<option value="' + employee.id + '">' + employee.employeeName + '</option>');
		});
		roles.forEach(function (role) {
			$role.append('<option value="' + role.id + '">' + role.roleName + '</option>');
		});
		types.forEach(function (type) {
			$type.append('<option value="' + type.name + '">' + type.name + '</option>');
		});
	}

	function buildFormExportEE() {
		$('.field-export-ee__item--select', $formExportEE).empty();
		var $project = $('.field-export-ee__project', $formExportEE);
		var $roles = $('.field-export-ee__role', $formExportEE);

		$project.append('<option value="">-- Select --</option>');
		$roles.append('<option value="">-- Select --</option>');

		projectsOfLoginUser.forEach(function (project) {
			$project.append('<option value="' + project.id + '">' + project.projectName + '</option>');
		});
		roles.forEach(function (role) {
			$roles.append('<option value="' + role.id + '">' + role.roleName + '</option>');
		});
	}
	function transformDataTableAllocation(allocations) {
		return allocations.map(function (allocation) {
			allocation.startDate = CommonUtils.formatDateToString(allocation.startDate);
			allocation.endDate = CommonUtils.formatDateToString(allocation.endDate);
			allocation.projectNameDisplay = (allocation.projectName || '') + (allocation.oppName || '');
			allocation.pmNameDisplay = (allocation.pmName || '') + (allocation.planPmName || '');
			return allocation;
		});
	}

	function buildFormFieldRole($selector, listChecked) {
		roles.forEach(function (item) {
			var $div = $('<div></div>').addClass('checkbox form-search-checkbox');
			var $label = $('<label></label>');
			$label.append('<input type="checkbox" name="role" data-field="roles" data-type="number" class="form-control__item form-control__item-role" value="' + item.id + '"/>' + item.roleName);
			var $input = $label.find('input');

			if (listChecked && ~listChecked.indexOf(item.roleName)) {
				$input.prop('checked', true);
			}

			$selector.append($div.append($label));
		});
	}

	function searchAllocation() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildModelRequestBody($('.form-search-lesson-learns').find('.form-control__item'));

		CommonUtils.postApi('/api/allocations/search', searchModal, 'POST').then(function (employees) {
			CommonUtils.hideLoading();
			$tableAllocation.setPage(1);
			$tableAllocation.setItemsCore(transformDataTableAllocation(employees));
		});
	}

	function onChangeEmployee() {
		var user = CommonUtils.findUserByUserId(employees, $(this).val());
		var $allocationRole = $('.field-allocation__role', $formActionAllocation);
		var roleId = user.roles && user.roles[0] && user.roles[0].id || $allocationRole.val();
		$allocationRole.val(roleId);

	}

	function getValueForCell(header, item) {
		var rangeDate = header.value;
		var allocations = getAllocationFromDate(rangeDate, item);

		var startDateAllocation = CommonUtils.formatStringToDate($('.form-control__start-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var endDateAllocation = CommonUtils.formatStringToDate($('.form-control__end-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));


		if (!allocations.length) {
			return '';
		} else {
			var $divContainer = $('<div></div>');

			allocations.forEach(function (allocation) {
				var $div = $('<div></div>');

				if (moment(startDateAllocation).isAfter(rangeDate.from, 'day')) {
					var strEnd = moment(rangeDate.to).isSameOrBefore(allocation.endDate) ? rangeDate.to : allocation.endDate;
					var strStart = moment(startDateAllocation).isSameOrBefore(allocation.startDate, 'day') ? allocation.startDate : startDateAllocation;

					$div.append($('<div></div>').text(CommonUtils.formatDateToString(strStart).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(strEnd).slice(0, 5)));
				} else if (moment(rangeDate.to).isAfter(endDateAllocation, 'day')) {
					var strStart = moment(rangeDate.from).isSameOrBefore(allocation.startDate, 'day') ? allocation.startDate : rangeDate.from;

					$div.append($('<div></div>').text(CommonUtils.formatDateToString(strStart).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(endDateAllocation).slice(0, 5)));
				} else if (moment(allocation.endDate).isBefore(rangeDate.to, 'day')) {
					var strStart = moment(allocation.startDate).isSameOrBefore(rangeDate.from, 'day') ? rangeDate.from : allocation.startDate;

					$div.append($('<div></div>').text(CommonUtils.formatDateToString(strStart).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(allocation.endDate).slice(0, 5)));
				} else if (moment(allocation.endDate).isAfter(rangeDate.to, 'day')) {
					if (moment(allocation.startDate).isAfter(rangeDate.from, 'day')) {
						$div.append($('<div></div>').text(CommonUtils.formatDateToString(allocation.startDate).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(rangeDate.to).slice(0, 5)));
					}
				}

				$div.append($('<div></div>').text('Allo: ' + allocation.allocation + '%'));
				$divContainer.append($div);
			})

			return $divContainer;
		}
	}

	function getAllocationFromDate(rangeDate, item) {
		return allocationDetails.filter(function (allocation) {
			var isStartDate = moment(allocation.startDate).isSameOrBefore(rangeDate.from, 'day') && moment(rangeDate.from).isSameOrBefore(allocation.endDate, 'day');
			var isEndDate = moment(allocation.startDate).isSameOrBefore(rangeDate.to, 'day') && moment(rangeDate.to).isSameOrBefore(allocation.endDate, 'day');
			var isContainRange = moment(rangeDate.from).isSameOrBefore(allocation.startDate, 'day') && moment(allocation.endDate).isSameOrBefore(rangeDate.to, 'day');

			return allocation.projectId === item.projectId && allocation.employeeId === item.employeeId && (isStartDate || isEndDate || isContainRange);
		})
	}

	function openModalViewAllocation() {
		$modalViewAllocationDetail.modal('show');
		$('.form_list_employee, .form_list_project', $modalViewAllocationDetail).val('').trigger('change');
		$('.form-control__date', $modalViewAllocationDetail).val('');
		$('.form-control__start-date', $modalViewAllocationDetail).val(moment().format('DD/MM/YYYY'));
		$('.form-control__end-date', $modalViewAllocationDetail).val(moment().add(1, 'months').format('DD/MM/YYYY'));
		$tableAllocationDetails.$control.closest('.table-wrapper').hide();
		viewAllocation();
	}

	function viewAllocation() {
		var employeeName = $('#employeeNamePopup').val();
		var projectName = $('#projectNamePopup').val();
		var pmName = $('#pmNamePopup').val();
		var startDate = $('.form-control__start-date').val();
		var endDate = $('.form-control__end-date').val();
		var checkFreeEmployee = $('#btnCheckFreeEmployee').is(":checked");
		var rolesPopUp = [];
		$.each($("input[name='role']:checked"), function(){
			rolesPopUp.push(parseInt($(this).val()));
		});
		// var roles =  $('.form-control__item-roles').is(":checked");
		if (startDate && endDate) {
			var request = {
				employeeName: employeeName,
				projectName: projectName,
				pmName: pmName,
				startDate: startDate,
				endDate: endDate,
				checkFreeEmployee: checkFreeEmployee,
				roles: rolesPopUp
			};
			CommonUtils.postApi('/api/allocations/detail', request, 'POST').then(function (arr) {
				$tableAllocationDetails.setPage(1);
				$tableAllocationDetails.setItemsCore(transformDataTableAllocationDetails(arr));
				initHeaderAllocation();
				CommonUtils.hideLoading();
				$tableAllocationDetails.$control.closest('.table-wrapper').show();
			})
		}
	}

	function initHeaderAllocation() {
		var startDate = CommonUtils.formatStringToDate($('.form-control__start-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var endDate = CommonUtils.formatStringToDate($('.form-control__end-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));

		var headers = [];

		if (startDate && endDate && moment(startDate).isBefore(endDate, 'day')) {
			var startWeek = CommonUtils.startOfWeek(startDate.clone());
			var tempWeek = startWeek.clone();

			while (moment(startWeek).isSameOrBefore(endDate, 'day')) {
				var nextWeek = tempWeek.addDays(7);
				var tempEndWeek = nextWeek.clone().minusDays(1);
				var tempStartWeek;
				var tempDisplayEndWeek;

				if (moment(startWeek).isBefore(startDate, 'days')) {
					tempStartWeek = startDate.clone();
				}

				if (moment(tempEndWeek).isAfter(endDate, 'days')) {
					tempDisplayEndWeek = endDate.clone();
				}

				headers.push({
					label: CommonUtils.formatDateToString(tempStartWeek || startWeek),
					value: {
						from: tempStartWeek && tempStartWeek.clone() || startWeek.clone(),
						to: tempDisplayEndWeek && tempDisplayEndWeek.clone() || tempEndWeek
					}
				});

				tempStartWeek = undefined;
				startWeek.addDays(7);
			}
		}

		$tableAllocationDetails.addNewHeader(headers);
	}

	function handleChangeItem(items) {
		var employeeName = $('#employeeNamePopup').val();
		var projectName = $('#projectNamePopup').val();
		var pmName = $('#pmNamePopup').val();
		var startDate = $('.form-control__start-date').val();
		var endDate = $('.form-control__end-date').val();
		if (startDate && endDate) {
			CommonUtils.showLoading();
			var request = {
				employeeName: employeeName,
				projectName: projectName,
				pmName: pmName,
				startDate: startDate,
				endDate: endDate
			};
		}
		var $deferred = $.Deferred();
		CommonUtils.postApi('/api/allocations/employee', request, 'POST').then(function (mockAllocationRes) {
			allocationDetails = transformDataAllocation(mockAllocationRes);
			$deferred.resolve();
		})

		return $deferred.promise();
	}

	function buildRequestBodyGetAllocation(items) {
		var objectDate = {
			startDate: $('.form-control__start-date').val(),
			endDate: $('.form-control__end-date').val()
		};

		return items.map(function (allocation) {
			return $.extend({}, objectDate, {
				employeeId: allocation.employeeId,
				projectId: allocation.projectId
			});
		});
	}

	function transformDataTableAllocationDetails(allocationDetails) {
		return allocationDetails.map(function (item) {
			item.startDate = CommonUtils.formatDateToString(item.startDate);
			item.endDate = CommonUtils.formatDateToString(item.endDate);
			item.projectNameDisplay = (item.projectName || '') + (item.oppName || '');
			item.pmNameDisplay = (item.pmName || '') + (item.planPmName || '');
			return item;
		})
	}

	function transformDataAllocation(allocations) {
		return allocations.map(function (item) {
			item.startDate = new Date(item.startDate);
			item.endDate = new Date(item.endDate);
			item.projectNameDisplay = (item.projectName || '') + (item.oppName || '');
			item.pmNameDisplay = (item.pmName || '') + (item.planPmName || '');
			return item;
		})
	}

	function buildUserField($selector) {
		$selector.empty();
		$selector.append('<option value="">-- Select --</option>');

		allEmployees.forEach(function (employee) {
			$selector.append('<option value="' + employee.id + '">' + employee.employeeName + '</option>');
		});
	}

	function buildProjectField($selector) {
		$selector.empty();
		$selector.append('<option value="">-- Select --</option>');

		projects.forEach(function (project) {
			$selector.append('<option value="' + project.id + '">' + project.projectName + '</option>');
		});
	}

	function onResetSearchAllocation() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildEmptyModelRequestBodyAllocations($('.form-search-allocation').find('.form-control__item'));
		$("input[name='role']").each(function () {
			$(this).prop( "checked", false );
		});
		$("input[name='type']").each(function () {
			$(this).prop( "checked", false );
		});
		$('.field-search').val("");
		CommonUtils.postApi('/api/allocations/search', searchModal, 'POST').then(function (allocations) {
			CommonUtils.hideLoading();
			$tableAllocation.setPage(1);
			$tableAllocation.setItemsCore(transformDataTableAllocation(allocations));
		});
	}

	$(document).ready(function () {
		$("#import-allocation").change(function (e) {
			e.preventDefault();
			var file_data = $('#import-allocation').prop('files')[0];
			var form_data = new FormData();
			form_data.append('file', file_data);
			$.ajax({
				url: "/api/allocations/import-allocation",
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
					CommonModal.message({message: err.responseText, noAutoClose: true});
				}
			});
		});
	});

	$(document).ready(function () {
		$("#import-allocation-by-month").change(function (e) {
			e.preventDefault();
			var file_data = $('#import-allocation-by-month').prop('files')[0];
			var form_data = new FormData();
			form_data.append('file', file_data);
			$.ajax({
				url: "/api/allocations/upload-allo-by-month",
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

	$(document).ready(function () {
		$("#import-allocation-by-date").change(function (e) {
			e.preventDefault();
			var file_data = $('#import-allocation-by-date').prop('files')[0];
			var form_data = new FormData();
			form_data.append('file', file_data);
			$.ajax({
				url: "/api/allocations/import-allocation-by-day",
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

	$(document).ready(function () {
		$("#import-allocation-by-period").change(function (e) {
			e.preventDefault();
			var file_data = $('#import-allocation-by-period').prop('files')[0];
			var form_data = new FormData();
			form_data.append('file', file_data);
			$.ajax({
				url: "/api/allocations/import-allocation-by-period",
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

	function buildFormCheckboxFieldsType($selector, listChecked, inputName) {
		buildFormCheckFields($selector, listChecked, types, {
			field: 'type',
			name: inputName || 'type',
			value: 'name',
			type: 'string',
		});
	}

	function buildFormRadioFieldsType($selector, listChecked, inputName) {
		buildFormCheckFields($selector, listChecked, types, {
			inputType: 'radio',
			field: 'type',
			name: inputName || 'type',
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
			inputType: 'checkbox'
		}, options);

		list.forEach(function (item) {
			var $div = $('<div></div>').addClass(options.inputType + ' form-search-' + options.inputType);
			var $label = $('<label></label>');
			$label.append('<input type="' + options.inputType + '"' +
				'name="' + options.name + '"' +
				'data-field="' + options.field + '"' +
				'data-type="' + options.type + '"' +
				'class="form-control__item"' +
				'value="' + item[options.value] + '"/>' + item[options.label]);

			var $input = $label.find('input');

			if (listChecked && (options.inputType === 'radio' ?
				~listChecked.indexOf(item[options.value]) :
				~listChecked.indexOf(item[options.label]))) {
				$input.prop('checked', true);
			}

			$selector.append($div.append($label));
		});
	}

	function onExportAllocationByDay() {
		CommonUtils.getApi('/api/allocations/export/day', 'GET').then(function (res) {
			window.open('/api/allocations/export/day/download/xlsx?', '_blank');
		}, function (error) {
			var messages = error.map(function (item) { return item.message; }).join(', ');
			CommonModal.message({message: messages, noAutoClose: true});
		});
	}

	function onExportAllocationByMonth() {
		CommonUtils.getApi('/api/allocations/export/month', 'GET').then(function (res) {
			window.open('/api/allocations/export/month/download/xlsx?', '_blank');
		}, function (error) {
			var messages = error.map(function (item) { return item.message; }).join(', ');
			CommonModal.message({message: messages, noAutoClose: true});
		});
	}

	function onExportAllocationByPeriod() {
		CommonUtils.getApi('/api/allocations/export/period', 'GET').then(function (res) {
			window.open('/api/allocations/export/period/download/xlsx?', '_blank');
		}, function (error) {
			var messages = error.map(function (item) { return item.message; }).join(', ');
			CommonModal.message({message: messages, noAutoClose: true});
		});
	}

	function openForm() {
		document.getElementById("myForm").style.display = "block";
	}

	function closeForm() {
		document.getElementById("myForm").style.display = "none";
	}

    var tableFieldsAllocation = [
        {
            name: 'Employee',
            valueKey: 'employeeName',
            canBeSort: true,
            click: function (e, item) {
                openViewEmployee(item);
            }
        },
        {
            name: 'Position',
            valueKey: 'roleName',
            canBeSort: true
        },
        {
            name: 'Type',
            valueKey: 'type',
            canBeSort: true
        },
        {
            name: 'Rate',
            valueKey: 'rate',
            canBeSort: true
        },
        {
            name: 'Main Skill',
            valueKey: 'mainSkill',
            canBeSort: true,
            isTextArea: true
        },
        {
            name: 'Project',
            valueKey: 'projectNameDisplay',
            canBeSort: true,
            click: function (e, item) {
                openFormViewProject(item);
            }
        },
        {
            name: 'Allocation',
            valueKey: 'allo',
            canBeSort: true
        },
        {
            name: 'PM',
            valueKey: 'pmNameDisplay',
            canBeSort: true
        },
        {
            name: 'Start Date',
            valueKey: 'startDate',
            canBeSort: true
        },
        {
            name: 'End Date',
            valueKey: 'endDate',
            canBeSort: true
        },
        {
            name: 'Note',
            valueKey: 'note',
            canBeSort: true
        },
        {
            name: 'Source',
            valueKey: 'source',
            canBeSort: true
        }
    ];
    var tableFieldsAllocationDetails = [
        {
            name: 'Employee',
            valueKey: 'employeeName',
            canBeSort: true
        },
        {
            name: 'Position',
            valueKey: 'roleName',
            canBeSort: true
        },
        {
            name: 'Main Skill',
            valueKey: 'mainSkill',
            canBeSort: true,
            isTextArea: true
        },
        {
            name: 'Project',
            valueKey: 'projectNameDisplay',
            canBeSort: true
        },
        {
            name: 'PM',
            valueKey: 'pmNameDisplay',
            canBeSort: true
        }
    ];
    var $tableAllocation;
    var $modalActionEmployee = $('#modalActionEmployee');
    var $formActionAllocation = $('.formActionAllocation');
    var $modalActionAllocation = $('#modalActionAllocation');
    var $formExportEE = $('.form-export-ee');
    var $formImport = $('.form-import');
    var $modalExportEE = $('#modal-export-ee');
    var $modalImport = $('#modalImport');
    var $modalViewAllocationDetail = $('#modalViewAllocationDetail');
    var $tableAllocationDetails;
    var roles = [];
    var projects = [];
    var projectsOfLoginUser = [];
    var opportunities = [];
    var employees = [];
    var customers = [];
    var allocationDetails = [];
    var allEmployees = [];
    var types = [];
    var contractTypes = [];
    var ranks = [];
    var statuses = [];
    var userRolePms = [];
    var userRoleQA = [];
    var $modalExportAllocation = $('#modalExportAllocation');
    var $modalActionProject = $('#modalActionProject');
    var $formActionProject = $('.formActionProject');

    /* prepare data */
    $(document).ready(function () {
        initEventHandleAllocation();
        initTableAllocation();
        initValidateFormActionAllocation();
        bulkFetchDataAllocation();
    });

    function initEventHandleAllocation() {
        $('#btnSearchAllocation').on('click', searchAllocation);
        $('#addAllocation').on('click', openFormCreateAllocation);
        $('#btnViewAllocation').on('click', openModalViewAllocation);
        $('#btnViewAllocationDetail').on('click', viewAllocation);
        $('.field-allocation__employee', $formActionAllocation).on('change', onChangeEmployee);
        $('#btnExportAllocation').on('click', openFormExportAllocation);

        $('.btnEditAllocation').on('click', onEditAllocation);
        $('.btnAddAllocation').on('click', onAddAllocation);
        $('.field-allocation__end-date, .field-allocation__start-date').on('change', validateRangeDate);
        $('.field-allocation__employee, .field-allocation__project, .form-search-user, .form-search-project').combobox();
        $('#btnResetSearchAllocation').on('click', onResetSearchAllocation);

        $('#btnViewExportEE').on('click', openFormExportEE);
        $('#btnViewImport').on('click', openFormImport);
        $('.btnExportEE').on('click', onExportEE);
        $('#btnExportAllocationByDay').on('click', onExportAllocationByDay);
        $('#btnExportAllocationByMonth').on('click', onExportAllocationByMonth);
        $('#btnExportAllocationByPeriod').on('click', onExportAllocationByPeriod);
        $('#fromStartDate').on('keyup', onEnterSearchAllocation);
        $('#toStartDate').on('keyup', onEnterSearchAllocation);
        $('#fromEndDate').on('keyup', onEnterSearchAllocation);
        $('#toEndDate').on('keyup', onEnterSearchAllocation);
        $('#projectName').on('keypress', onEnterSearchAllocation);
        $('#employeeName').on('keypress', onEnterSearchAllocation);
        $('#pmName').on('keypress', onEnterSearchAllocation);
        $('#startRate').on('keypress', onEnterSearchAllocation);
        $('#endRate').on('keypress', onEnterSearchAllocation);
    }

    function onEnterSearchAllocation(e) {
        var p = e.which;
        if (p == 13) {
            searchAllocation();
        }
    }

    function initTableAllocation() {
        $tableAllocation = $('#tableAllocation').dataTable({
            fields: tableFieldsAllocation,
            showDefaultAction: true,
            onEditItem: openFormEditAllocation,
            onDeleteItem: confirmDeleteAllocation,
            getRowColor: getRowColor,
            showColumnNo: true,
            pagingByClient: true,
            sortByClient: true,

        });

        $tableAllocationDetails = $('#tableAllocationDetails').dataTable({
            fields: tableFieldsAllocationDetails,
            getValueForCell: getValueForCell,
            showColumnNo: true,
            pagingByClient: true,
            sortByClient: true,
            handleChangeItem: handleChangeItem,
            getRowColor: getRowColor,
            fixedColumns: {
                leftColumns: 3,
            }
        });

    }

    function getRowColor(rowItem) {
        if (rowItem.status === '0') {
            return 'red';

        }

        return '#000';
    }

    function fetchListAllocation() {
        CommonUtils.getApi('/api/allocations').then(function (data) {
            $tableAllocation.setItemsCore(transformDataTableAllocation(data));
        });
    }

    function getProject(projectId, callback) {
        $.ajax({
            url: '/api/projects/' + projectId,
            type: "GET",
            dataType: "json",
            success: callback
        });
    }

    function getEmployee(employeeId, callback) {
        $.ajax({
            url: '/api/employees/' + employeeId,
            type: "GET",
            dataType: "json",
            success: callback
        });
    }


    function openViewEmployee(allocation) {
        getEmployee(allocation.employeeId, handleOpenViewEmployee);
    }

    function openFormViewProject(allocation) {
        getProject(allocation.projectId, handlerSetProjectValues);
    }

    function handlerSetProjectValues(project) {
        handleopenFormViewProject(project,contractTypes,customers,ranks,statuses,userRoleQA,userRolePms);
    }

    function bulkFetchDataAllocation() {
        $.when(
            CommonUtils.getApi('/api/allocations'),
            CommonUtils.getApi('/api/employees/roles'),
            CommonUtils.getApi('/api/employees/listAllContainResignedEmployee'),
            CommonUtils.getApi('/api/projects'),
            CommonUtils.getApi('/api/projects/projectsOfLoginUser'),
            CommonUtils.getApi('/api/employees/listAllContainResignedEmployee'),
            CommonUtils.getApi('/api/opportunities'),
            CommonUtils.getApi('/api/allocations/type'),
            CommonUtils.getApi('/api/opportunities/type/contracts'),
            CommonUtils.getApi('/api/customers'),
            CommonUtils.getApi('/api/customers/appParams'),
            CommonUtils.getApi('/api/employees'),
            CommonUtils.getApi('/api/employees'),
            CommonUtils.getApi('/api/employees/status')
        ).then(function (allocations, listRoles, listEmployee, listProject, listProjectOfLoginUser, listAllContainResignedEmployee, listOpportunity,
                         listType, contractTypesRes, customersRes, appParams, listPm, listQA,statuses) {
            roles = listRoles;
            employees = listEmployee;
            projects = listProject;
            projectsOfLoginUser = listProjectOfLoginUser;
            allEmployees = listAllContainResignedEmployee;
            opportunities = listOpportunity;
            types = listType;
            buildFormFieldRole($(".form-control__item--roles"));
            buildUserField($("#employeeNamePopup"));
            buildProjectField($("#projectNamePopup"));
            buildUserField($("#pmNamePopup"));
            buildFormCheckboxFieldsType($(".form-control__item--types"));
            $tableAllocation.setItemsCore(transformDataTableAllocation(allocations));
            contractTypes = contractTypesRes;
            customers = customersRes;
            ranks = CommonUtils.getCustomTypeFromAppParams(appParams, 'PROJECT_RANK_TYPE');
            statuses = CommonUtils.getCustomTypeFromAppParams(appParams, 'PROJECT_STATUS');
            userRolePms = listPm;
            userRoleQA = listQA;
            listRole=listRoles;
            listStatus = statuses;
        })
    }

    function initValidateFormActionAllocation() {
        $formActionAllocation.validate({
            messages: {
                employeeId: {
                    required: CommonMessage.getMessageRequired('Employee')
                },
                roleId: {
                    required: CommonMessage.getMessageRequired('Role')
                },
                allo: {
                    required: CommonMessage.getMessageRequired('Allocation')
                },
                rate: {
                    required: CommonMessage.getMessageRequired('Rate')
                },
                startDate: {
                    required: CommonMessage.getMessageRequired('Start Date')
                },
                endDate: {
                    required: CommonMessage.getMessageRequired('End Date')
                }
            }
        });
    }

    function onAddAllocation() {
        var allocationModel = CommonUtils.buildModelRequestBody($formActionAllocation.find('.form-control__item'));
        var formValid = $formActionAllocation.valid();

        if (formValid && validateRangeDate()) {
            CommonUtils.showLoading();

            CommonUtils.postApi('/api/allocations', allocationModel, 'POST').then(function (res) {
                CommonUtils.hideLoading();
                $modalActionAllocation.modal('hide');
                $tableAllocation.setPage(1);
                fetchListAllocation();
                CommonModal.message({message: 'Create allocation successfully!'});
            }, function (error) {
                var messages = error.map(function (item) {
                    return item.message;
                }).join(', ');
                CommonModal.message({message: messages, noAutoClose: true});
            });
        }
    }

    function onEditAllocation() {
        var allocationModel = CommonUtils.buildModelRequestBody($formActionAllocation.find('.form-control__item'));
        var formValid = $formActionAllocation.valid();

        if (formValid && validateRangeDate()) {
            CommonUtils.showLoading();
            allocationModel.id = $('.btnEditAllocation').data('id');

            CommonUtils.postApi('/api/allocations', allocationModel, 'POST').then(function (res) {
                CommonUtils.hideLoading();
                $modalActionAllocation.modal('hide');
                fetchListAllocation();
                CommonModal.message({message: 'Edit allocation successfully!'});
            }, function (error) {
                var messages = error.map(function (item) {
                    return item.message;
                }).join(', ');
                CommonModal.message({message: messages, noAutoClose: true});
            });
        }
    }

    function validateRangeDate() {
        var startDate = CommonUtils.formatStringToDate($('.field-allocation__start-date', $formActionAllocation).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
        var endDate = CommonUtils.formatStringToDate($('.field-allocation__end-date', $formActionAllocation).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
        var validDate = startDate && endDate && startDate.getTime() < endDate.getTime();

        if (startDate && endDate) {
            $('#endDate-error-minDate').toggle(!validDate);
            $('.field-allocation__start-date').toggleClass('error', !validDate);
        }

        return validDate;
    }

    function validateExportEERangeDate() {
        var startDate = CommonUtils.formatStringToDate($('.field-export-ee__start-date', $formExportEE).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
        var endDate = CommonUtils.formatStringToDate($('.field-export-ee__end-date', $formExportEE).val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
        var validDate = startDate && endDate && startDate.getTime() < endDate.getTime();

        if (startDate && endDate) {
            $('#ee-endDate-error-minDate').toggle(!validDate);
            $('.field-export-ee__start-date').toggleClass('error', !validDate);
        }

        if (startDate === null && endDate === null) {
            return true;
        }
        return validDate;
    }

    function onDeleteAllocation(item) {
        CommonUtils.showLoading();
        CommonUtils.postApi('/api/allocations/' + item.id, null, 'DELETE').then(function (res) {
            CommonUtils.hideLoading();
            fetchListAllocation();
            CommonModal.message({message: 'Delete allocation successfully!'});
        });
    }


    function openFormEditAllocation(allocation) {
        $('.btnEditAllocation').show();
        $('.btnAddAllocation').hide();
        handleDefaultFormActionAllocation();
        $('.modal-title', $modalActionAllocation).text('Edit Allocation');
        $('.field-allocation__project', $formActionAllocation).val(allocation.projectId).trigger('change');
        $('.field-allocation__opportunity', $formActionAllocation).val(allocation.oppId).trigger('change');
        $('.field-allocation__employee', $formActionAllocation).val(allocation.employeeId).trigger('change');
        $('.field-allocation__role', $formActionAllocation).val(allocation.roleId).trigger('change');
        $('.field-allocation__allo', $formActionAllocation).val(allocation.allo).trigger('change');
        $('.field-allocation__rate', $formActionAllocation).val(allocation.rate).trigger('change');
        $('.field-allocation__start-date', $formActionAllocation).val(allocation.startDate).trigger('change');
        $('.field-allocation__end-date', $formActionAllocation).val(allocation.endDate).trigger('change');
        $('.field-allocation__note', $formActionAllocation).val(allocation.note).trigger('change');
        $('.field-allocation__source', $formActionAllocation).val(allocation.source).trigger('change');
        $('.field-allocation__type', $formActionAllocation).val(allocation.type).trigger('change');
        $('.btnEditAllocation').data('id', allocation.id);

        $modalActionAllocation.modal('show');
    }

    function openFormCreateAllocation() {
        $('.btnEditAllocation').hide();
        $('.btnAddAllocation').show();
        $('.modal-title', $modalActionAllocation).text('Create Allocation');
        $formActionAllocation.find('.form-control').val('').trigger('change');
        handleDefaultFormActionAllocation();
        $modalActionAllocation.modal('show');
    }

    function openFormImport() {
        $('.modal-title', $modalImport).text('Import');
        $formImport.find('.form-control').val('').trigger('change');
        $modalImport.modal('show');
    }

    function openFormExportAllocation() {
        $('.modal-title', $modalExportAllocation).text('Export Allocation');
        $formActionAllocation.find('.form-control').val('').trigger('change');
        $modalExportAllocation.modal('show');
    }

    function openFormExportEE() {
        $('.modal-title', $modalExportEE).text('Export EE');
        $formActionAllocation.find('.form-control').val('').trigger('change');
        handleDefaultFormExportEE();
        $modalExportEE.modal('show');
    }

    function onExportEE() {
        var exportEEModal = CommonUtils.buildModelRequestBody($formExportEE.find('.form-control__item'));
        var projectId = $('.field-export-ee__item--select').val();

        if (projectId !== null && projectId !== '' && validateExportEERangeDate()) {
            CommonUtils.postApi('/api/allocations/export-ee', exportEEModal, 'POST').then(function (res) {
                CommonUtils.hideLoading();
                if (validateExportEERangeDate()) {
                    window.open('/api/allocations/export-ee?', '_blank');
                }
            }, function (error) {
                var messages = error.map(function (item) {
                    return item.message;
                }).join(', ');
                CommonModal.message({message: messages, noAutoClose: true});
            });
        }
    }

    function handleDefaultFormActionAllocation() {
        $formActionAllocation.validate().resetForm();
        buildFormActionAllocation();
        $('.form-control__item', $formActionAllocation).removeClass('error');
        $('#endDate-error-minDate', $formActionAllocation).hide();
    }

    function handleDefaultFormExportEE() {
        $formExportEE.validate().resetForm();
        buildFormExportEE();
        $('.form-control__item', $formExportEE).removeClass('error');
        $('#ee-endDate-error-minDate', $formExportEE).hide();
    }

    function confirmDeleteAllocation(item) {
        CommonModal.confirm({
            title: 'Delete Employee',
            message: 'Are you sure to delete this allocation?',
            confirmed: onDeleteAllocation.bind(this, item)
        });
    }

    function buildFormActionAllocation() {
        $('.field-allocation__item--select', $formActionAllocation).empty();
        var $project = $('.field-allocation__project', $formActionAllocation);
        var $opportunity = $('.field-allocation__opportunity', $formActionAllocation);
        var $employee = $('.field-allocation__employee', $formActionAllocation);
        var $role = $('.field-allocation__role', $formActionAllocation);
        var $type = $('.field-allocation__type', $formActionAllocation);

        $project.append('<option value="">-- Select --</option>');
        $opportunity.append('<option value="">-- Select --</option>');
        $employee.append('<option value="">-- Select --</option>');
        $role.append('<option value="">-- Select --</option>');
        $type.append('<option value="">-- Select --</option>');

        projects.forEach(function (project) {
            $project.append('<option value="' + project.id + '">' + project.projectName + '</option>');
        });
        opportunities.forEach(function (opportunity) {
            $opportunity.append('<option value="' + opportunity.id + '">' + opportunity.oppName + '</option>');
        });
        employees.forEach(function (employee) {
            $employee.append('<option value="' + employee.id + '">' + employee.employeeName + '</option>');
        });
        roles.forEach(function (role) {
            $role.append('<option value="' + role.id + '">' + role.roleName + '</option>');
        });
        types.forEach(function (type) {
            $type.append('<option value="' + type.name + '">' + type.name + '</option>');
        });
    }

    function buildFormExportEE() {
        $('.field-export-ee__item--select', $formExportEE).empty();
        var $project = $('.field-export-ee__project', $formExportEE);
        var $roles = $('.field-export-ee__role', $formExportEE);

        $project.append('<option value="">-- Select --</option>');
        $roles.append('<option value="">-- Select --</option>');

        projectsOfLoginUser.forEach(function (project) {
            $project.append('<option value="' + project.id + '">' + project.projectName + '</option>');
        });
        roles.forEach(function (role) {
            $roles.append('<option value="' + role.id + '">' + role.roleName + '</option>');
        });
    }

    function transformDataTableAllocation(allocations) {
        return allocations.map(function (allocation) {
            allocation.startDate = CommonUtils.formatDateToString(allocation.startDate);
            allocation.endDate = CommonUtils.formatDateToString(allocation.endDate);
            allocation.projectNameDisplay = (allocation.projectName || '') + (allocation.oppName || '');
            allocation.pmNameDisplay = (allocation.pmName || '') + (allocation.planPmName || '');
            return allocation;
        });
    }

    function buildFormFieldRole($selector, listChecked) {
        roles.forEach(function (item) {
            var $div = $('<div></div>').addClass('checkbox form-search-checkbox');
            var $label = $('<label></label>');
            $label.append('<input type="checkbox" name="role" data-field="roles" data-type="number" class="form-control__item form-control__item-role" value="' + item.id + '"/>' + item.roleName);
            var $input = $label.find('input');

            if (listChecked && ~listChecked.indexOf(item.roleName)) {
                $input.prop('checked', true);
            }

            $selector.append($div.append($label));
        });
    }

    function searchAllocation() {
        CommonUtils.showLoading();
        var searchModal = CommonUtils.buildModelRequestBody($('.form-search-lesson-learns').find('.form-control__item'));

        CommonUtils.postApi('/api/allocations/search', searchModal, 'POST').then(function (employees) {
            CommonUtils.hideLoading();
            $tableAllocation.setPage(1);
            $tableAllocation.setItemsCore(transformDataTableAllocation(employees));
        });
    }

    function onChangeEmployee() {
        var user = CommonUtils.findUserByUserId(employees, $(this).val());
        var $allocationRole = $('.field-allocation__role', $formActionAllocation);
        var roleId = user.roles && user.roles[0] && user.roles[0].id || $allocationRole.val();
        $allocationRole.val(roleId);

    }

    function getValueForCell(header, item) {
        var rangeDate = header.value;
        var allocations = getAllocationFromDate(rangeDate, item);

        var startDateAllocation = CommonUtils.formatStringToDate($('.form-control__start-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
        var endDateAllocation = CommonUtils.formatStringToDate($('.form-control__end-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));


        if (!allocations.length) {
            return '';
        } else {
            var $divContainer = $('<div></div>');

            allocations.forEach(function (allocation) {
                var $div = $('<div></div>');

                if (moment(startDateAllocation).isAfter(rangeDate.from, 'day')) {
                    var strEnd = moment(rangeDate.to).isSameOrBefore(allocation.endDate) ? rangeDate.to : allocation.endDate;
                    var strStart = moment(startDateAllocation).isSameOrBefore(allocation.startDate, 'day') ? allocation.startDate : startDateAllocation;

                    $div.append($('<div></div>').text(CommonUtils.formatDateToString(strStart).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(strEnd).slice(0, 5)));
                } else if (moment(rangeDate.to).isAfter(endDateAllocation, 'day')) {
                    var strStart = moment(rangeDate.from).isSameOrBefore(allocation.startDate, 'day') ? allocation.startDate : rangeDate.from;

                    $div.append($('<div></div>').text(CommonUtils.formatDateToString(strStart).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(endDateAllocation).slice(0, 5)));
                } else if (moment(allocation.endDate).isBefore(rangeDate.to, 'day')) {
                    var strStart = moment(allocation.startDate).isSameOrBefore(rangeDate.from, 'day') ? rangeDate.from : allocation.startDate;

                    $div.append($('<div></div>').text(CommonUtils.formatDateToString(strStart).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(allocation.endDate).slice(0, 5)));
                } else if (moment(allocation.endDate).isAfter(rangeDate.to, 'day')) {
                    if (moment(allocation.startDate).isAfter(rangeDate.from, 'day')) {
                        $div.append($('<div></div>').text(CommonUtils.formatDateToString(allocation.startDate).slice(0, 5) + ' - ' + CommonUtils.formatDateToString(rangeDate.to).slice(0, 5)));
                    }
                }

                $div.append($('<div></div>').text('Allo: ' + allocation.allocation + '%'));
                $divContainer.append($div);
            })

            return $divContainer;
        }
    }

    function getAllocationFromDate(rangeDate, item) {
        return allocationDetails.filter(function (allocation) {
            var isStartDate = moment(allocation.startDate).isSameOrBefore(rangeDate.from, 'day') && moment(rangeDate.from).isSameOrBefore(allocation.endDate, 'day');
            var isEndDate = moment(allocation.startDate).isSameOrBefore(rangeDate.to, 'day') && moment(rangeDate.to).isSameOrBefore(allocation.endDate, 'day');
            var isContainRange = moment(rangeDate.from).isSameOrBefore(allocation.startDate, 'day') && moment(allocation.endDate).isSameOrBefore(rangeDate.to, 'day');

            return allocation.projectId === item.projectId && allocation.employeeId === item.employeeId && (isStartDate || isEndDate || isContainRange);
        })
    }

    function openModalViewAllocation() {
        $modalViewAllocationDetail.modal('show');
        $('.form_list_employee, .form_list_project', $modalViewAllocationDetail).val('').trigger('change');
        $('.form-control__date', $modalViewAllocationDetail).val('');
        $('.form-control__start-date', $modalViewAllocationDetail).val(moment().format('DD/MM/YYYY'));
        $('.form-control__end-date', $modalViewAllocationDetail).val(moment().add(1, 'months').format('DD/MM/YYYY'));
        $tableAllocationDetails.$control.closest('.table-wrapper').hide();
        viewAllocation();
    }

    function handleDefaultFormActionProject() {
        $formActionProject.validate().resetForm();
        $formActionProject.find('.form-control__item').removeClass('error');
        $formActionProject.find('#startDate-error-minDate').hide();
        buildContractTypeField($('.field-project__contract-type', $formActionProject));
        buildCustomerField();
        buildUserFieldFormActionProject();
    }

    function buildUserFieldFormActionProject() {
        $('.field-project__pm', $formActionProject).empty();
        $('.field-project__qa', $formActionProject).empty();
        $('.field-project__pm', $formActionProject).append('<option value="">-- Select --</option>');
        $('.field-project__qa', $formActionProject).append('<option value="">-- Select --</option>');

        userRoleQA.forEach(function (user) {
            $('.field-project__qa', $formActionProject).append('<option value="' + user.id + '">' + user.employeeName + '</option>');
        });
        userRolePms.forEach(function (user) {
            $('.field-project__pm', $formActionProject).append('<option value="' + user.id + '">' + user.employeeName + '</option>');
        });
    }

    function buildCustomerField() {
        $('.field-project__customer', $formActionProject).empty();
        $('.field-project__rank', $formActionProject).empty();
        $('.field-project__status', $formActionProject).empty();
        $('.field-project__customer', $formActionProject).append('<option value="">-- Select --</option>');
        $('.field-project__rank', $formActionProject).append('<option value="">-- Select --</option>');
        $('.field-project__status', $formActionProject).append('<option value="">-- Select --</option>');

        customers.forEach(function (customer) {
            $('.field-project__customer', $formActionProject).append('<option value="' + customer.id + '">' + customer.customerName + '</option>');
        });
        ranks.forEach(function (rank) {
            $('.field-project__rank', $formActionProject).append('<option value="' + rank.code + '">' + rank.name + '</option>');
        });
        statuses.forEach(function (status) {
            $('.field-project__status', $formActionProject).append('<option value="' + status.name + '">' + status.name + '</option>');
        });
    }

    function buildContractTypeField($selector) {
        $selector.empty();
        $selector.append('<option value="">-- Select --</option>');

        contractTypes.forEach(function (contractType) {
            $selector.append('<option value="' + contractType.id + '">' + contractType.name + '</option>');
        });
    }

    function viewAllocation() {
        var employeeName = $('#employeeNamePopup').val();
        var projectName = $('#projectNamePopup').val();
        var pmName = $('#pmNamePopup').val();
        var startDate = $('.form-control__start-date').val();
        var endDate = $('.form-control__end-date').val();
        var checkFreeEmployee = $('#btnCheckFreeEmployee').is(":checked");
        var rolesPopUp = [];
        $.each($("input[name='role']:checked"), function () {
            rolesPopUp.push(parseInt($(this).val()));
        });
        // var roles =  $('.form-control__item-roles').is(":checked");
        if (startDate && endDate) {
            var request = {
                employeeName: employeeName,
                projectName: projectName,
                pmName: pmName,
                startDate: startDate,
                endDate: endDate,
                checkFreeEmployee: checkFreeEmployee,
                roles: rolesPopUp
            };
            CommonUtils.postApi('/api/allocations/detail', request, 'POST').then(function (arr) {
                $tableAllocationDetails.setPage(1);
                $tableAllocationDetails.setItemsCore(transformDataTableAllocationDetails(arr));
                initHeaderAllocation();
                CommonUtils.hideLoading();
                $tableAllocationDetails.$control.closest('.table-wrapper').show();
            })
        }
    }

    function initHeaderAllocation() {
        var startDate = CommonUtils.formatStringToDate($('.form-control__start-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
        var endDate = CommonUtils.formatStringToDate($('.form-control__end-date').val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));

        var headers = [];

        if (startDate && endDate && moment(startDate).isBefore(endDate, 'day')) {
            var startWeek = CommonUtils.startOfWeek(startDate.clone());
            var tempWeek = startWeek.clone();

            while (moment(startWeek).isSameOrBefore(endDate, 'day')) {
                var nextWeek = tempWeek.addDays(7);
                var tempEndWeek = nextWeek.clone().minusDays(1);
                var tempStartWeek;
                var tempDisplayEndWeek;

                if (moment(startWeek).isBefore(startDate, 'days')) {
                    tempStartWeek = startDate.clone();
                }

                if (moment(tempEndWeek).isAfter(endDate, 'days')) {
                    tempDisplayEndWeek = endDate.clone();
                }

                headers.push({
                    label: CommonUtils.formatDateToString(tempStartWeek || startWeek),
                    value: {
                        from: tempStartWeek && tempStartWeek.clone() || startWeek.clone(),
                        to: tempDisplayEndWeek && tempDisplayEndWeek.clone() || tempEndWeek
                    }
                });

                tempStartWeek = undefined;
                startWeek.addDays(7);
            }
        }

        $tableAllocationDetails.addNewHeader(headers);
    }

    function handleChangeItem(items) {
        var employeeName = $('#employeeNamePopup').val();
        var projectName = $('#projectNamePopup').val();
        var pmName = $('#pmNamePopup').val();
        var startDate = $('.form-control__start-date').val();
        var endDate = $('.form-control__end-date').val();
        if (startDate && endDate) {
            CommonUtils.showLoading();
            var request = {
                employeeName: employeeName,
                projectName: projectName,
                pmName: pmName,
                startDate: startDate,
                endDate: endDate
            };
        }
        var $deferred = $.Deferred();
        CommonUtils.postApi('/api/allocations/employee', request, 'POST').then(function (mockAllocationRes) {
            allocationDetails = transformDataAllocation(mockAllocationRes);
            $deferred.resolve();
        })

        return $deferred.promise();
    }

    function buildRequestBodyGetAllocation(items) {
        var objectDate = {
            startDate: $('.form-control__start-date').val(),
            endDate: $('.form-control__end-date').val()
        };

        return items.map(function (allocation) {
            return $.extend({}, objectDate, {
                employeeId: allocation.employeeId,
                projectId: allocation.projectId
            });
        });
    }

    function transformDataTableAllocationDetails(allocationDetails) {
        return allocationDetails.map(function (item) {
            item.startDate = CommonUtils.formatDateToString(item.startDate);
            item.endDate = CommonUtils.formatDateToString(item.endDate);
            item.projectNameDisplay = (item.projectName || '') + (item.oppName || '');
            item.pmNameDisplay = (item.pmName || '') + (item.planPmName || '');
            return item;
        })
    }

    function transformDataAllocation(allocations) {
        return allocations.map(function (item) {
            item.startDate = new Date(item.startDate);
            item.endDate = new Date(item.endDate);
            item.projectNameDisplay = (item.projectName || '') + (item.oppName || '');
            item.pmNameDisplay = (item.pmName || '') + (item.planPmName || '');
            return item;
        })
    }

    function buildUserField($selector) {
        $selector.empty();
        $selector.append('<option value="">-- Select --</option>');

        allEmployees.forEach(function (employee) {
            $selector.append('<option value="' + employee.id + '">' + employee.employeeName + '</option>');
        });
    }

    function buildProjectField($selector) {
        $selector.empty();
        $selector.append('<option value="">-- Select --</option>');

        projects.forEach(function (project) {
            $selector.append('<option value="' + project.id + '">' + project.projectName + '</option>');
        });
    }

    function onResetSearchAllocation() {
        CommonUtils.showLoading();
        var searchModal = CommonUtils.buildEmptyModelRequestBodyAllocations($('.form-search-allocation').find('.form-control__item'));
        $("input[name='role']").each(function () {
            $(this).prop("checked", false);
        });
        $("input[name='type']").each(function () {
            $(this).prop("checked", false);
        });
        $('.field-search').val("");
        CommonUtils.postApi('/api/allocations/search', searchModal, 'POST').then(function (allocations) {
            CommonUtils.hideLoading();
            $tableAllocation.setPage(1);
            $tableAllocation.setItemsCore(transformDataTableAllocation(allocations));
        });
    }

    $(document).ready(function () {
        $("#import-allocation").change(function (e) {
            e.preventDefault();
            var file_data = $('#import-allocation').prop('files')[0];
            var form_data = new FormData();
            form_data.append('file', file_data);
            $.ajax({
                url: "/api/allocations/import-allocation",
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
                    CommonModal.message({message: err.responseText, noAutoClose: true});
                }
            });
        });
    });

    $(document).ready(function () {
        $("#import-allocation-by-month").change(function (e) {
            e.preventDefault();
            var file_data = $('#import-allocation-by-month').prop('files')[0];
            var form_data = new FormData();
            form_data.append('file', file_data);
            $.ajax({
                url: "/api/allocations/upload-allo-by-month",
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

    $(document).ready(function () {
        $("#import-allocation-by-date").change(function (e) {
            e.preventDefault();
            var file_data = $('#import-allocation-by-date').prop('files')[0];
            var form_data = new FormData();
            form_data.append('file', file_data);
            $.ajax({
                url: "/api/allocations/import-allocation-by-day",
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

    $(document).ready(function () {
        $("#import-allocation-by-period").change(function (e) {
            e.preventDefault();
            var file_data = $('#import-allocation-by-period').prop('files')[0];
            var form_data = new FormData();
            form_data.append('file', file_data);
            $.ajax({
                url: "/api/allocations/import-allocation-by-period",
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

    function buildFormCheckboxFieldsType($selector, listChecked, inputName) {
        buildFormCheckFields($selector, listChecked, types, {
            field: 'type',
            name: inputName || 'type',
            value: 'name',
            type: 'string',
        });
    }

    function buildFormRadioFieldsType($selector, listChecked, inputName) {
        buildFormCheckFields($selector, listChecked, types, {
            inputType: 'radio',
            field: 'type',
            name: inputName || 'type',
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
            inputType: 'checkbox'
        }, options);

        list.forEach(function (item) {
            var $div = $('<div></div>').addClass(options.inputType + ' form-search-' + options.inputType);
            var $label = $('<label></label>');
            $label.append('<input type="' + options.inputType + '"' +
                'name="' + options.name + '"' +
                'data-field="' + options.field + '"' +
                'data-type="' + options.type + '"' +
                'class="form-control__item"' +
                'value="' + item[options.value] + '"/>' + item[options.label]);

            var $input = $label.find('input');

            if (listChecked && (options.inputType === 'radio' ?
                ~listChecked.indexOf(item[options.value]) :
                ~listChecked.indexOf(item[options.label]))) {
                $input.prop('checked', true);
            }

            $selector.append($div.append($label));
        });
    }

    function onExportAllocationByDay() {
        CommonUtils.getApi('/api/allocations/export/day', 'GET').then(function (res) {
            window.open('/api/allocations/export/day/download/xlsx?', '_blank');
        }, function (error) {
            var messages = error.map(function (item) {
                return item.message;
            }).join(', ');
            CommonModal.message({message: messages, noAutoClose: true});
        });
    }

    function onExportAllocationByMonth() {
        CommonUtils.getApi('/api/allocations/export/month', 'GET').then(function (res) {
            window.open('/api/allocations/export/month/download/xlsx?', '_blank');
        }, function (error) {
            var messages = error.map(function (item) {
                return item.message;
            }).join(', ');
            CommonModal.message({message: messages, noAutoClose: true});
        });
    }

    function onExportAllocationByPeriod() {
        CommonUtils.getApi('/api/allocations/export/period', 'GET').then(function (res) {
            window.open('/api/allocations/export/period/download/xlsx?', '_blank');
        }, function (error) {
            var messages = error.map(function (item) {
                return item.message;
            }).join(', ');
            CommonModal.message({message: messages, noAutoClose: true});
        });
    }

    function openForm() {
        document.getElementById("myForm").style.display = "block";
    }

    function closeForm() {
        document.getElementById("myForm").style.display = "none";
    }
});