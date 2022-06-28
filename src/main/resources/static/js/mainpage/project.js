$(function () {
	var tableFields = [
		{
			name: 'Project',
			valueKey: 'projectName',
			canBeSort: true
		},
		{
			name: 'Contract type',
			valueKey: 'contractTypeName',
			canBeSort: true
		},
		{
			name: 'Rank',
			valueKey: 'rankName',
			canBeSort: true
		},
		{
			name: 'Customer',
			valueKey: 'customerName',
			canBeSort: true
		},
		{
			name: 'PM',
			valueKey: 'pmName',
			canBeSort: true
		},
		{
			name: 'QA',
			valueKey: 'qaName',
			canBeSort: true
		},
		{
			name: 'Headcount project size',
			valueKey: 'headCountSize',
			canBeSort: true
		},
		{
			name: 'Project size (MM)',
			valueKey: 'mmSize',
			canBeSort: true
		},
		{
			name: 'Start date',
			valueKey: 'startDate',
			canBeSort: true
		},
		{
			name: 'End date',
			valueKey: 'endDate',
			canBeSort: true
		},
		{
			name: 'Renew contract',
			valueKey: 'renewContract',
			canBeSort: true
		},
		{
			name: 'Project Overview',
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
			valueKey: 'status',
			canBeSort: true
		},
		{
			name: 'Technical Skill',
			valueKey: 'technicalSkill',
			canBeSort: true
		}
	];

	var $tableProjects;
	var $formActionProject = $('.formActionProject');
	var $modalActionProject = $('#modalActionProject');
	var ranks = [];
	var users = [];
	var contractTypes = [];
	var customers = [];
	var userRolePms = [];
	var userRoleQA = [];
	var statuses = []

	/* prepare data */
	$(document).ready(function () {
		initEventHandleProject();
		initTableProject();
		initValidateFormActionProject();
		bulkFetchDataProject();
	});

	function initEventHandleProject() {
		$('#btnSearchEmployee').on('click', searchProject);
		$('#btnAddProject').on('click', openFormCreateProject);
		$('.btnEditProject').on('click', onEditProject);
		$('.btnAddProject').on('click', onAddProject);
		$('.field-project__start-date, .field-project__end-date').on('change', toggleErrorMinDate);
		$('.form-search-control__customer, .form-search-control__pm, .field-project__pm, .field-project__customer,.form-search-control__status').combobox();
		$('#btnResetProject').on('click', onResetSearchProject);
		$('#projectName').on('keypress', onEnterSearchProject);
	}
	function onEnterSearchProject(e) {
		if(e.which === 13){searchProject();}
	}
	function initTableProject() {
		$tableProjects = $('#tableProject').dataTable({
			fields: tableFields,
			showDefaultAction: true,
			onEditItem: openFormEditProject,
			onDeleteItem: confirmDeleteProject,
			onViewItem:openFormEditProject,
			showColumnNo: true,
			pagingByClient: true,
			sortByClient: true,
			actions: [{
				icon: 'glyphicon-eye-open',
				funcAction: openFormViewProject
			}]
		});
	}

	function fetchListProjects() {
		CommonUtils.getApi('/api/projects').then(function (data) {
			$tableProjects.setItemsCore(transformDataTableProject(data));
		});
	}

	function bulkFetchDataProject() {
		CommonUtils.showLoading();
		$.when(
			CommonUtils.getApi('/api/projects'),
			CommonUtils.getApi('/api/employees'),
			CommonUtils.getApi('/api/customers'),
			CommonUtils.getApi('/api/customers/appParams'),
			CommonUtils.getApi('/api/opportunities/type/contracts'),
			CommonUtils.getApi('/api/employees'),
			CommonUtils.getApi('/api/employees'),
		).then(function (projects, usersRes, customersRes, appParams, contractTypesRes, listPm, listQA) {
			CommonUtils.hideLoading();
			users = usersRes;
			customers = customersRes;
			contractTypes = contractTypesRes;
			userRolePms = listPm;
			userRoleQA = listQA;
			ranks = CommonUtils.getCustomTypeFromAppParams(appParams, 'PROJECT_RANK_TYPE');
			statuses = CommonUtils.getCustomTypeFromAppParams(appParams, 'PROJECT_STATUS');
			$tableProjects.setItemsCore(transformDataTableProject(projects));
			buildFormSearchProject();
		})
	}

	function initValidateFormActionProject() {
		$formActionProject.validate({
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
				},
				status: {
					required: CommonMessage.getMessageRequired('Status')
				}
			}
		});
	}

	function onAddProject() {
		var projectModel = CommonUtils.buildModelRequestBody($formActionProject.find('.form-control__item'));

		if (handleValidateFromActionProject()) {
			CommonUtils.showLoading();

			CommonUtils.postApi('/api/projects', projectModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionProject.modal('hide');
				$tableProjects.setPage(1);
				fetchListProjects();
				CommonModal.message({ message: 'Create project successfully!' });
			});
		}
	}

	function onEditProject() {
		var projectModel = CommonUtils.buildModelRequestBody($formActionProject.find('.form-control__item'));

		if (handleValidateFromActionProject()) {
			CommonUtils.showLoading();
			projectModel.id = $('.btnEditProject').data('id');

			CommonUtils.postApi('/api/projects/', projectModel, 'POST').then(function (res) {
				CommonUtils.hideLoading();
				$modalActionProject.modal('hide');
				fetchListProjects();
				CommonModal.message({ message: 'Edit project successfully!' });
			});
		}
	}

	function handleValidateFromActionProject() {
		var formValid = $formActionProject.valid();
		var customValid = customValidateDateField($('.field-project__start-date'), $('.field-project__end-date'));

		$('#startDate-error-minDate', $formActionProject).toggle(!customValid);
		$('.field-project__start-date', $formActionProject).toggleClass('error', !customValid);

		return formValid && customValid;
	}

	function toggleErrorMinDate() {
		if ($('.field-project__start-date').val() && $('.field-project__end-date').val()) {
			var customValid = customValidateDateField($('.field-project__start-date'), $('.field-project__end-date'));

			$('#startDate-error-minDate', $formActionProject).toggle(!customValid);
			$('.field-project__start-date', $formActionProject).toggleClass('error', !customValid);
		}
	}

	function customValidateDateField($startDate, $endDate) {
		var startDate = CommonUtils.formatStringToDate($startDate.val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var endDate = CommonUtils.formatStringToDate($endDate.val().replace(CommonPattern.DATE_FORMAT, '$2/$1/$3'));
		var inValid = !startDate || !endDate || startDate.getTime() > endDate.getTime();

		return !inValid;
	}

	function onDeleteProject(item) {
		CommonUtils.showLoading();
		CommonUtils.postApi('/api/projects/' + item.id, null, 'DELETE').then(function (res) {
			CommonUtils.hideLoading();
			$tableProjects.handlePageWhenDeleteItem();
			fetchListProjects();
			CommonModal.message({ message: 'Delete project successfully!' });
		});
	}

	function openFormEditProject(project) {
		$('.btnEditProject').show();
		$('.btnAddProject').hide();
		$('.modal-title', $modalActionProject).text('Edit Project');
		$('#startDate-error-minDate', $formActionProject).hide();
		$('.btnEditProject').data('id', project.id);



		handleDefaultFormActionProject($formActionProject,contractTypes,customers,ranks,statuses,userRoleQA,userRolePms);
		$('.field-project__project-name', $formActionProject).val(project.projectName).trigger('change').prop("disabled",false);
		$('.field-project__contract-type', $formActionProject).val(project.contractTypeId).trigger('change').prop("disabled",false);
		$('.field-project__start-date', $formActionProject).val(project.startDate).trigger('change').prop("disabled",false);
		$('.field-project__end-date', $formActionProject).val(project.endDate).trigger('change').prop("disabled",false);
		$('.field-project__head-count-size', $formActionProject).val(project.headCountSize).trigger('change').prop("disabled",false);
		$('.field-project__mm-size', $formActionProject).val(project.mmSize).trigger('change').prop("disabled",false);
		$('.field-project__pm', $formActionProject).val(project.pmId).trigger('change').prop("disabled",false);
		$('.field-project__pm', $formActionProject).val(project.pmId).closest('.form-group').find('input').prop("disabled",false);
		$('.field-project__overview', $formActionProject).val(project.overview).trigger('change').prop("disabled",false);
		$('.field-project__note', $formActionProject).val(project.note).trigger('change').prop("disabled",false);
		$('.field-project__customer', $formActionProject).val(project.customerId).trigger('change').prop("disabled",false);
		$('.field-project__customer', $formActionProject).closest('.form-group').find('input').prop("disabled",false);

		$('.field-project__rank', $formActionProject).val(project.rank).trigger('change').prop("disabled",false);
		$('.field-project__qa', $formActionProject).val(project.qaId).trigger('change').prop("disabled",false);
		$('.field-project__renew-contract', $formActionProject).val(project.renewContract).trigger('change').prop("disabled",false);
		$('.field-project__status', $formActionProject).val(project.status).trigger('change').prop("disabled",false);
		$('.field-project__technicalSkill', $formActionProject).val(project.technicalSkill).trigger('change').prop("disabled",false);

		$modalActionProject.modal('show');
	}
	function openFormViewProject(project) {
		handleopenFormViewProject(project,contractTypes,customers,ranks,statuses,userRoleQA,userRolePms);
	}



	function openFormCreateProject() {
		$('.btnEditProject').hide();
		$('.btnAddProject').show();
		$('.modal-title', $modalActionProject).text('Create Project');
		$formActionProject.find('.form-control').val('').trigger('change');
		handleDefaultFormActionProject($formActionProject,contractTypes,customers,ranks,statuses,userRoleQA,userRolePms);
		$modalActionProject.modal('show');
	}



	function confirmDeleteProject(item) {
		CommonModal.confirm({
			title: 'Delete Project',
			message: 'Are you sure to delete this project?',
			confirmed: onDeleteProject.bind(this, item)
		});
	}

	function transformDataTableProject(projects) {
		return projects.map(function (project) {
			project.startDate = CommonUtils.formatDateToString(project.startDate);
			project.endDate = CommonUtils.formatDateToString(project.endDate);
			project.renewContract = CommonUtils.formatDateToString(project.renewContract);
			project.contractTypeName = CommonUtils.getContractNameByContractId(contractTypes, project.contractTypeId);
			project.pmName = CommonUtils.getUserNameByUserId(users, project.pmId);
			project.qaName = CommonUtils.getUserNameByUserId(users, project.qaId);
			project.customerName = CommonUtils.getCustomerNameByCustomerId(customers, project.customerId);

			return project;
		});
	}

	function buildFormSearchProject() {
		ranks.forEach(function (item) {
			$('.form-search-control__rank').append('<option value="' + item.code + '">' + item.name + '</option>');
		});

		customers.forEach(function (item) {
			$('.form-search-control__customer').append('<option value="' + item.id + '">' + item.customerName + '</option>');
		});

		userRolePms.forEach(function (item) {
			$('.form-search-control__pm').append('<option value="' + item.id + '">' + item.employeeName + '</option>');
		});

		contractTypes.forEach(function (item) {
			$('.form-search-control__contract-type').append('<option value="' + item.id + '">' + item.name + '</option>');
		});
		statuses.forEach(function (item) {
			$('.form-search-control__status').append('<option value="' + item.name + '">' + item.name + '</option>');
		});
	}

	function searchProject() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildModelRequestBody($('.form-search-project').find('.form-control__item'));

		CommonUtils.postApi('/api/projects/search', searchModal, 'POST').then(function (projects) {
			CommonUtils.hideLoading();
			$tableProjects.setPage(1);
			$tableProjects.setItemsCore(transformDataTableProject(projects));
		});
	}

	function onResetSearchProject() {
		CommonUtils.showLoading();
		var searchModal = CommonUtils.buildEmptyModelRequestBodyProjects($('.form-search-project').find('.form-control__item'));
		$('.field-search').val("");
		$('[name^="customer"], [name^="pm"]').val("");

		CommonUtils.postApi('/api/projects/search', searchModal, 'POST').then(function (projects) {
			CommonUtils.hideLoading();
			$tableProjects.setPage(1);
			$tableProjects.setItemsCore(transformDataTableProject(projects));
		});
	}
});
