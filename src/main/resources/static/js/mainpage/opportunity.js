$(function () {

	var tableFields = [
		{
			name: 'Opportunity Name',
			valueKey: 'oppName',
			canBeSort: true
		},
		{
			name: 'Contract Type',
			valueKey: 'contractName',
			canBeSort: true
		},
		{
			name: 'MM',
			valueKey: 'mm',
			canBeSort: true
		},
		{
			name: 'Total MM',
			valueKey: 'totalMm',
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
			name: 'Duration',
			valueKey: 'duration',
			canBeSort: true
		},
		{
			name: 'Success Rate',
			valueKey: 'successRate',
			canBeSort: true
		},
		{
			name: 'Temp PM',
			valueKey: 'tempPmName',
			canBeSort: true
		},
		{
			name: 'Plan PM',
			valueKey: 'planPmName',
			canBeSort: true
		},
		{
			name: 'Overview',
			valueKey: 'overview',
			isTextArea: true
		},
		{
			name: 'Note',
			valueKey: 'note',
			isTextArea: true
		},
		{
			name: 'Status',
			valueKey: 'statusString'
		}
	];
	var $tableOpportunity;
	var $modalActionOpportunity = $('#modalActionOpportunity');
	var $formActionOpportunity = $('.formActionOpportunity');
	var $modalOpportunityToProject = $('#modalOpportunityToProject');
	var $formOpportunityToProject = $('.formOpportunityToProject');
	var contractTypes = [];
	var users = [];
	var customers = [];
	var ranks = [];
	var userRolePms = [];
	var userRoleQA = [];

	/* prepare data */
	$(document).ready(function () {
		initEventHandleOpportunity();
		initTableOpportunity();
		initValidateFormActionOpportunity();
		initValidateFormConvertOpportunity();
		bulkFetchDataOpportunity();
	});

	function initEventHandleOpportunity() {
		$('#btnSearchOpportunity').on('click', searchOpportunity);
		$('#btnAddOpportunity').on('click', openFormCreateOpportunity);

		$('.field-opportunity__start-date, .field-opportunity__end-date').on('change', onChangePlanDate);
		$('.field-opportunity__contract-type, .field-opportunity__mm, .field-opportunity__duration').on('change', calculatorTotalMm);
		$('.btnEditOpportunity').on('click', onEditOpportunity);
		$('.btnAddOpportunity').on('click', onAddOpportunity);
		$('.btnConvertOpportunity').on('click', onConvertOpportunityToProject);
		$('.form-search-control__temp-pm, .form-search-control__plan-pm, .field-opportunity__temp-pm, .field-opportunity__plan-pm').combobox();
		$('#btnResetSearchOpportunity').on('click', onResetSearchOpportunities);
		$('#opportunityName').on('keypress', onEnterSearchOpportunity);
		$('#tempPm').on('keydown', onEnterSearchOpportunity);
		$('#planPm').on('onkeydown', onEnterSearchOpportunity);
		$('#fromStartDate').on('keyup', onEnterSearchOpportunity);
		$('#toStartDate').on('keyup', onEnterSearchOpportunity);
		$('#fromEndDate').on('keyup', onEnterSearchOpportunity);
		$('#toEndDate').on('keyup', onEnterSearchOpportunity);

	}

	function onEnterSearchOpportunity(e) {
		var p=e.which;
		if(p == 13){searchOpportunity();}
	}
	function initTableOpportunity() {
		$tableOpportunity = $('#tableOpportunity').dataTable({
			fields: tableFields,
			showDefaultAction: true,
			onEditItem: openFormEditOpportunity,
			onDeleteItem: confirmDeleteOpportunity,
			showColumnNo: true,
			pagingByClient: true,
			sortByClient: true,
			actions: [{
				icon: 'glyphicon-transfer',
				funcAction: convertToProject
			}]
		});
	}

	function fetchListOpportunity() {
		CommonUtils.getApi('/api/opportunities').then(function (opportunities) {
			$tableOpportunity.setItemsCore(transformDataTableOpportunity(opportunities));
		});
	}

	function bulkFetchDataOpportunity() {
		CommonUtils.showLoading();
		$.when(
			CommonUtils.getApi('/api/opportunities/type/contracts'),
			CommonUtils.getApi('/api/opportunities'),
			CommonUtils.getApi('/api/employees'),
			CommonUtils.getApi('/api/employees'),
			CommonUtils.getApi('/api/employees'),
			CommonUtils.getApi('/api/opportunities/appParams')
		).then(function (contractsTypeRes, opportunities, usersRes, pmList, qaList, statuses) {
			CommonUtils.hideLoading();
			contractTypes = contractsTypeRes;
			users = usersRes;
			userRolePms = pmList;
			userRoleQA = qaList;
			userStatus = statuses;
			buildFormCheckboxFieldsStatus($("#form-control__item--status"));
			buildUserField($('.form-search-control__temp-pm'), $('.form-search-control__plan-pm'), $('.form-search-user'));
			buildContractTypeField($('.form-search-control__contract-type'));
			$tableOpportunity.setItemsCore(transformDataTableOpportunity(opportunities));
		})
	}

	function initValidateFormActionOpportunity() {
		$formActionOpportunity.validate({
			messages: {
				oppName: {
					required: CommonMessage.getMessageRequired('Opportunity Name'),
				},
				contractTypeId: {
					required: CommonMessage.getMessageRequired('Contract Type')
				}
			}
		});
	}

	function initValidateFormConvertOpportunity() {
		$formOpportunityToProject.validate({
			messages: {
				contractTypeId: {
					required: CommonMessage.getMessageRequired('Contract Type'),
				},
				projectName: {
					required: CommonMessage.getMessageRequired('Project Name')
				},
				customerId: {
					required: CommonMessage.getMessageRequired('Customer')
				},
				rank: {
					required: CommonMessage.getMessageRequired('Rank')
				},
				headCountSize: {
					required: CommonMessage.getMessageRequired('HeadCount Project Size')
				},
				mmSize: {
					required: CommonMessage.getMessageRequired('MM Project Size')
				},
				startDate: {
					required: CommonMessage.getMessageRequired('Start Plan Date')
				},
				endDate: {
					required: CommonMessage.getMessageRequired('End Plan Date')
				},
				pmId: {
					required: CommonMessage.getMessageRequired('PM')
				}
			}
		});
	}

	function onAddOpportunity() {
		var employeeModel = CommonUtils.buildModelRequestBody($formActionOpportunity.find('.form-control__item'));

		if (handleValidateFromActionOpportunity()) {
			CommonUtils.showLoading();

			CommonUtils.postApi('/api/opportunities', employeeModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionOpportunity.modal('hide');
				$tableOpportunity.setPage(1);
				fetchListOpportunity();
				CommonModal.message({ message: 'Create opportunity successfully!' });
			}, function (error) {
				var messages = error.map(function (item) { return item.message; }).join(', ');
				CommonModal.message({message: messages, noAutoClose: true});
			});
		}
	}

	function onEditOpportunity() {
		var employeeModel = CommonUtils.buildModelRequestBody($formActionOpportunity.find('.form-control__item'));

		if (handleValidateFromActionOpportunity()) {
			CommonUtils.showLoading();
			employeeModel.id = $('.btnEditOpportunity').data('id');

			CommonUtils.postApi('/api/opportunities', employeeModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionOpportunity.modal('hide');
				fetchListOpportunity();
				CommonModal.message({ message: 'Edit opportunity successfully!' });
			}, function (error) {
				var messages = error.map(function (item) { return item.message; }).join(', ');
				CommonModal.message({message: messages, noAutoClose: true});
			});
		}
	}

	function handleValidateFromActionOpportunity() {
		var formValid = $formActionOpportunity.valid();
		var customValid = customValidateDateField();
		var statusInvalid = validateStatusField();

		if ($('.field-opportunity__start-date', $formActionOpportunity).val() && $('.field-opportunity__end-date', $formActionOpportunity).val()) {
			$('#endDate-error-minDate', $formActionOpportunity).toggle(!customValid);
			$('.field-opportunity__date', $formActionOpportunity).toggleClass('error', !customValid);
		}

		return formValid && customValid && statusInvalid;
	}

	function customValidateDateField($startDate, $endDate) {
		var period = getPeriodOpportunity($startDate, $endDate);
		var inValid = ( period.startDate && period.endDate && period.startDate.getTime() > period.endDate.getTime() ) ||
			( period.startDate && !period.endDate ) ||
			( !period.startDate && period.endDate );

		return !inValid;
	}

	function validateStatusField() {
		var status = $('.form-control__status input[type="radio"]:checked', $formActionOpportunity).val();

		$('.form-control__status-error', $formActionOpportunity).toggle(!status);
		$('.form-control__status', $formActionOpportunity).toggleClass('error', !status);

		return !!status;
	}

	function onDeleteOpportunity(item) {
		CommonUtils.showLoading();
		CommonUtils.postApi('/api/opportunities/' + item.id, null, 'DELETE').then(function (res) {
			CommonUtils.hideLoading();
			$tableOpportunity.handlePageWhenDeleteItem();
			fetchListOpportunity();
			CommonModal.message({ message: 'Delete opportunity successfully!' });
		});
	}

	function openFormEditOpportunity(opportunity) {
		$('.btnEditOpportunity').show();
		$('.btnAddOpportunity').hide();

		$('.modal-title', $modalActionOpportunity).text('Edit Opportunity');
		$('.btnEditOpportunity').data('id', opportunity.id);
		handleDefaultFormActionOpportunity();
		buildFormRadioFieldsStatus($('.form-control__status', $formActionOpportunity), [opportunity.status], opportunity.oppCode + 'Status');
		$('.form-control__status-error', $formActionOpportunity).hide();

		$('.field-opportunity__opportunity-code', $formActionOpportunity).val(opportunity.oppCode).trigger('change');
		$('.field-opportunity__opportunity-name', $formActionOpportunity).val(opportunity.oppName.substring(4)).trigger('change');
		$('.field-opportunity__contract-type', $formActionOpportunity).val(opportunity.contractType).trigger('change');
		$('.field-opportunity__mm', $formActionOpportunity).val(opportunity.mm).trigger('change');
		$('.field-opportunity__total-mm', $formActionOpportunity).val(opportunity.totalMm).trigger('change');
		$('.field-opportunity__start-date', $formActionOpportunity).val(opportunity.startDate).trigger('change');
		$('.field-opportunity__end-date', $formActionOpportunity).val(opportunity.endDate).trigger('change');
		$('.field-opportunity__duration', $formActionOpportunity).val(opportunity.duration).trigger('change');
		$('.field-opportunity__success-rate', $formActionOpportunity).val(opportunity.successRate).trigger('change');
		$('.field-opportunity__note', $formActionOpportunity).val(opportunity.note).trigger('change');
		$('.field-opportunity__temp-pm', $formActionOpportunity).val(opportunity.tempPm).trigger('change');
		$('.field-opportunity__plan-pm', $formActionOpportunity).val(opportunity.planPm).trigger('change');
		$('.field-opportunity__overview', $formActionOpportunity).val(opportunity.overview).trigger('change');
		$modalActionOpportunity.modal('show');
	}

	function openFormCreateOpportunity() {
		$('.btnEditOpportunity').hide();
		$('.btnAddOpportunity').show();
		$('.modal-title', $modalActionOpportunity).text('Create Opportunity');
		$formActionOpportunity.find('.form-control').val('').trigger('change');
		handleDefaultFormActionOpportunity();
		buildFormRadioFieldsStatus($('.form-control__status', $formActionOpportunity), [], 'createStatus');
		$('.form-control__status-error', $formActionOpportunity).hide();

		CommonUtils.getApi('/api/opportunities/generateCode').then(function (opportunityCode) {
			$formActionOpportunity.find('.field-opportunity__opportunity-code').val(opportunityCode);
			$modalActionOpportunity.modal('show');
		});
	}

	function handleDefaultFormActionOpportunity() {
		buildContractTypeField($('.field-opportunity__contract-type', $formActionOpportunity));
		$('.field-opportunity__user', $formActionOpportunity).empty();
		$('#endDate-error-minDate', $formActionOpportunity).hide();
		buildUserField($('.field-opportunity__temp-pm'), $('.field-opportunity__plan-pm'), $('.field-opportunity__user'));
		$formActionOpportunity.validate().resetForm();
		$formActionOpportunity.find('.form-control__item').removeClass('error');
	}

	function confirmDeleteOpportunity(item) {
		CommonModal.confirm({
			title: 'Delete Opportunity ',
			message: 'Are you sure to delete this opportunity?',
			confirmed: onDeleteOpportunity.bind(this, item)
		});
	}

	function onConvertOpportunityToProject() {
		var opportunity = CommonUtils.buildModelRequestBody($formOpportunityToProject.find('.form-control__item'));
		var formValid = $formOpportunityToProject.valid();
		var customValid = customValidateDateField($('.field-project__start-date'), $('.field-project__end-date'));

		$('#startDate-error-minDate', $formOpportunityToProject).toggle(!customValid);
		$('.field-project__start-date', $formOpportunityToProject).toggleClass('error', !customValid);

		if (formValid && customValid) {
			CommonUtils.showLoading();

			CommonUtils.postApi('/api/opportunities/toProject', opportunity, 'POST').then(function () {
				CommonUtils.hideLoading();
				$modalOpportunityToProject.modal('hide');
				fetchListOpportunity();
				CommonModal.message({ message: 'Create project successfully!' });
			})
		}
	}

	function convertToProject(opportunity) {
		if (!customers.length || ranks.length) {
			$.when(
				CommonUtils.getApi('/api/customers'),
				CommonUtils.getApi('/api/customers/appParams')
			).then(function (customersRes, appParams) {
				customers = customersRes;
				ranks = CommonUtils.getCustomTypeFromAppParams(appParams, 'PROJECT_RANK_TYPE');

				openFormConvertOpportunityToProject(opportunity);
			})
		} else {
			openFormConvertOpportunityToProject(opportunity);
		}
	}

	function openFormConvertOpportunityToProject(opportunity) {
		$formOpportunityToProject.find('.form-control').val('').trigger('change');
		$formOpportunityToProject.validate().resetForm();
		buildContractTypeField($('.field-project__contract-type', $formOpportunityToProject));
		$('#startDate-error-minDate', $formOpportunityToProject).hide();
		buildCustomerField();
		buildUserFieldFormActionProject();

		$('.field-project__opportunity-id', $formOpportunityToProject).val(opportunity.id).trigger('change');
		$('.field-project__opportunity-name', $formOpportunityToProject).val(opportunity.oppName).trigger('change');
		$('.field-project__project-name', $formOpportunityToProject).val(opportunity.oppName.replace(/Opp_/, '')).trigger('change');
		$('.field-project__contract-type', $formOpportunityToProject).val(opportunity.contractType).trigger('change');
		$('.field-project__start-date', $formOpportunityToProject).val(opportunity.startDate).trigger('change');
		$('.field-project__end-date', $formOpportunityToProject).val(opportunity.endDate).trigger('change');
		$('.field-project__head-count-size', $formOpportunityToProject).val(opportunity.totalMm).trigger('change');
		$('.field-project__mm-size', $formOpportunityToProject).val(opportunity.mm).trigger('change');
		$('.field-project__pm', $formOpportunityToProject).val(opportunity.planPm).trigger('change');
		$('.field-project__overview', $formOpportunityToProject).val(opportunity.overview).trigger('change');
		$('.field-project__note', $formOpportunityToProject).val(opportunity.note).trigger('change');

		$modalOpportunityToProject.modal('show');
	}

	function buildContractTypeField($selector) {
		$selector.empty();
		$selector.append('<option value="">-- Select --</option>');

		contractTypes.forEach(function (contractType) {
			$selector.append('<option value="' + contractType.id + '">' + contractType.name + '</option>');
		});
	}

	function buildCustomerField() {
		$('.field-project__customer', $formOpportunityToProject).empty();
		$('.field-project__rank', $formOpportunityToProject).empty();
		$('.field-project__customer', $formOpportunityToProject).append('<option value="">-- Select --</option>');
		$('.field-project__rank', $formOpportunityToProject).append('<option value="">-- Select --</option>');

		customers.forEach(function (customer) {
			$('.field-project__customer', $formOpportunityToProject).append('<option value="' + customer.id + '">' + customer.customerName + '</option>');
		});
		ranks.forEach(function (rank) {
			$('.field-project__rank', $formOpportunityToProject).append('<option value="' + rank.code + '">' + rank.name + '</option>');
		});
		console.log(rank);
	}

	function buildUserFieldFormActionProject() {
		$('.field-project__pm', $formOpportunityToProject).empty();
		$('.field-project__qa', $formOpportunityToProject).empty();
		$('.field-project__pm', $formOpportunityToProject).append('<option value="">-- Select --</option>');
		$('.field-project__qa', $formOpportunityToProject).append('<option value="">-- Select --</option>');

		userRolePms.forEach(function (user) {
			$('.field-project__pm', $formOpportunityToProject).append('<option value="' + user.id + '">' + user.employeeName + '</option>');
		});

		userRoleQA.forEach(function (user) {
			$('.field-project__qa', $formOpportunityToProject).append('<option value="' + user.id + '">' + user.employeeName + '</option>');
		});
	}

	function buildUserField($tempPm, $planPm, $user) {
		$tempPm.append('<option value="">-- Select --</option>');
		$planPm.append('<option value="">-- Select --</option>');

		userRolePms.forEach(function (user) {
			$user.append('<option value="' + user.id + '">' + user.employeeName + '</option>');
		});
	}

	function transformDataTableOpportunity(opportunities) {
		return opportunities.map(function (opportunity) {
			opportunity.contractName = CommonUtils.getContractNameByContractId(contractTypes, opportunity.contractType)
			opportunity.tempPmName = CommonUtils.getUserNameByUserId(users, opportunity.tempPm);
			opportunity.planPmName = CommonUtils.getUserNameByUserId(users, opportunity.planPm);
			opportunity.startDate = CommonUtils.formatDateToString(opportunity.startDate);
			opportunity.endDate = CommonUtils.formatDateToString(opportunity.endDate);

			return opportunity;
		});
	}

	function onChangePlanDate() {
		var period = getPeriodOpportunity();

		if (period.endDate && period.startDate) {
			var months = period.endDate.getMonth() - period.startDate.getMonth() + (12 * (period.endDate.getFullYear() - period.startDate.getFullYear()));

			if (period.endDate.getDate() < period.startDate.getDate()) {
				months--;
			}

			if (months || period.endDate.getMonth() === period.startDate.getMonth()) {
				var padMonth = +((period.endDate.getDate() - period.startDate.getDate()) / period.endDate.getTotalDaysOfMonth()).toFixed(1);

				months += padMonth;
			} else {
				var padMonth = +((period.endDate.getDate() + (period.startDate.getTotalDaysOfMonth() - period.startDate.getDate())) / 30).toFixed(1);

				months += padMonth;
			}

			$('.field-opportunity__duration', $formActionOpportunity).val(months >= 0 ? months : 0);
			$('.field-opportunity__duration', $formActionOpportunity).trigger('change');

			var customValid = customValidateDateField();

			$('#endDate-error-minDate', $formActionOpportunity).toggle(!customValid)
			$('.field-opportunity__start-date', $formActionOpportunity).toggleClass('error', !customValid)
		} else {
			$('.field-opportunity__duration', $formActionOpportunity).val(0);
		}

	}

	function buildFormCheckboxFieldsStatus($selector, listChecked, inputName) {
		buildFormCheckFields($selector, listChecked, userStatus, {
			field: 'status',
			name: inputName || 'status'
		});
	}

	function buildFormRadioFieldsStatus($selector, listChecked, inputName) {
		$selector.empty();

		buildFormCheckFieldsPopup($selector, listChecked, userStatus, {
			inputType: 'radio',
			field: 'status',
			name: inputName || 'status'
		});

		$('.form-control__status input[type="radio"]', $formActionOpportunity).on('change', function () {
			validateStatusField();
		});
	}

	function buildFormCheckFields($selector, listChecked, list, options) {
		listChecked = (listChecked || []).map(function (val) {
			return (val || '').toString();
		});

		options = $.extend({
			value: 'code',
			label: 'name',
			type: 'string',
			field: '',
			name: '',
			inputType: 'checkbox'
		}, options);

		for (let i = 0; i < list.length; i++) {
			var item = list[i];
			var $div = $('<div></div>').addClass(options.inputType + ' form-search-' + options.inputType);
			var $label = $('<label></label>');
			if (item.code === '1' || item.code === '2' || item.code === '3') {
				$label.append('<input type="' + options.inputType + '"' +
					'name="' + options.name + '"' +
					'data-field="' + options.field + '"' +
					'data-type="' + options.type + '"' +
					'class="form-control__item"' +
					'checked ' +
					'value="' + item[options.value] + '"/>' + item[options.label]);
			} else {
				$label.append('<input type="' + options.inputType + '"' +
					'name="' + options.name + '"' +
					'data-field="' + options.field + '"' +
					'data-type="' + options.type + '"' +
					'class="form-control__item"' +
					'value="' + item[options.value] + '"/>' + item[options.label]);
			}

			var $input = $label.find('input');

			if (listChecked && (options.inputType === 'radio' ?
				~listChecked.indexOf(item[options.value]) :
				~listChecked.indexOf(item[options.label]))) {
				$input.prop('checked', true);
			}

			$selector.append($div.append($label));
		}
	}

	function buildFormCheckFieldsPopup($selector, listChecked, list, options) {
		listChecked = (listChecked || []).map(function (val) {
			return (val || '').toString();
		});

		options = $.extend({
			value: 'code',
			label: 'name',
			type: 'string',
			field: '',
			name: '',
			inputType: 'checkbox'
		}, options);

		for (let i = 0; i < list.length; i++) {
			var item = list[i];
			var $div = $('<div></div>').addClass(options.inputType + ' form-search-' + options.inputType);
			var $label = $('<label></label>');
			if (item.code === '1') {
				$label.append('<input type="' + options.inputType + '"' +
					'name="' + options.name + '"' +
					'data-field="' + options.field + '"' +
					'data-type="' + options.type + '"' +
					'class="form-control__item"' +
					'checked ' +
					'value="' + item[options.value] + '"/>' + item[options.label]);
			} else {
				$label.append('<input type="' + options.inputType + '"' +
					'name="' + options.name + '"' +
					'data-field="' + options.field + '"' +
					'data-type="' + options.type + '"' +
					'class="form-control__item"' +
					'value="' + item[options.value] + '"/>' + item[options.label]);
			}

			var $input = $label.find('input');

			if (listChecked && (options.inputType === 'radio' ?
				~listChecked.indexOf(item[options.value]) :
				~listChecked.indexOf(item[options.label]))) {
				$input.prop('checked', true);
			}

			$selector.append($div.append($label));
		}
	}

	function getPeriodOpportunity($startDate, $endDate) {
		var $controlStartDate = $startDate || $('.field-opportunity__start-date', $formActionOpportunity);
		var $controlEndDate = $endDate || $('.field-opportunity__end-date', $formActionOpportunity);

		return {
			startDate: CommonUtils.formatStringToDate($controlStartDate.val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3')),
			endDate: CommonUtils.formatStringToDate($controlEndDate.val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3')),
		}
	}

	function calculatorTotalMm() {
		var contractId = $('.field-opportunity__contract-type', $formActionOpportunity).val();
		var duration = $('.field-opportunity__duration', $formActionOpportunity).val();
		var mm = $('.field-opportunity__mm', $formActionOpportunity).val();

		if (CommonUtils.getContractNameByContractId(contractTypes, contractId).toUpperCase() === 'LABO') {
			$('.field-opportunity__total-mm', $formActionOpportunity).val((duration * mm).toFixed(1) || 0);
		} else {
			$('.field-opportunity__total-mm', $formActionOpportunity).val(mm);
		}
	}

	function searchOpportunity() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildModelRequestBody($('.form-search-opportunity').find('.form-control__item'));
		CommonUtils.postApi('/api/opportunities/search', searchModal, 'POST').then(function (opportunities) {
			CommonUtils.hideLoading();
			$tableOpportunity.setPage(1);
			$tableOpportunity.setItemsCore(transformDataTableOpportunity(opportunities));
		});
	}


	function onResetSearchOpportunities() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildEmptyModelRequestBodyOpportunities($('.form-search-opportunity').find('.form-control__item'));

		$('.field-search').val("");
		$('[name^="tempPm"], [name^="planPm"]').val("");
		$("input[name='status']").each(function () {
			if($(this).val() == 1 || $(this).val() == 2 || $(this).val() == 3){
				$(this).prop( "checked", true );
			}else
			{
				$(this).prop( "checked", false );
			}
		});
		CommonUtils.postApi('/api/opportunities/search', searchModal, 'POST').then(function (opportunities) {
			CommonUtils.hideLoading();
			$tableOpportunity.setPage(1);
			$tableOpportunity.setItemsCore(transformDataTableOpportunity(opportunities));
		});
	}
});
