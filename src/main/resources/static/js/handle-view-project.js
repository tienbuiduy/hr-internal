function handleopenFormViewProject(project,contractTypes,customers,ranks,statuses,userRoleQA,userRolePms) {
    var $formActionProject = $('.formActionProject');
    var $modalActionProject = $('#modalActionProject');
    $('.btnEditProject').hide();
    $('.btnAddProject').hide();
    $('.modal-title', $modalActionProject).text('View Project');
    $('#startDate-error-minDate', $formActionProject).hide();
    $('.btnEditProject').data('id', project.id);
    handleDefaultFormActionProject($formActionProject,contractTypes,customers,ranks,statuses,userRoleQA,userRolePms);
    $('.field-project__project-name', $formActionProject).val(project.projectName).trigger('change').prop("disabled",true);
    $('.field-project__contract-type', $formActionProject).val(project.contractTypeId).trigger('change').prop("disabled",true);
    $('.field-project__start-date', $formActionProject).val(project.startDate).trigger('change').prop("disabled",true);
    $('.field-project__end-date', $formActionProject).val(project.endDate).trigger('change').prop("disabled",true);
    $('.field-project__head-count-size', $formActionProject).val(project.headCountSize).trigger('change').prop("disabled",true);
    $('.field-project__mm-size', $formActionProject).val(project.mmSize).trigger('change').prop("disabled",true);
    $('.field-project__pm', $formActionProject).val(project.pmId).trigger('change').prop("disabled",true);
    $('.field-project__pm', $formActionProject).val(project.pmId).closest('.form-group').find('input').prop("disabled",true);
    $('.field-project__overview', $formActionProject).val(project.overview).trigger('change').prop("disabled",true);
    $('.field-project__note', $formActionProject).val(project.note).trigger('change').prop("disabled",true);
    $('.field-project__customer', $formActionProject).val(project.customerId).trigger('change').prop("disabled",true);
    $('.field-project__customer', $formActionProject).closest('.form-group').find('input').prop("disabled",true);

    $('.field-project__rank', $formActionProject).val(project.rank).trigger('change').prop("disabled",true);
    $('.field-project__qa', $formActionProject).val(project.qaId).trigger('change').prop("disabled",true);
    $('.field-project__renew-contract', $formActionProject).val(project.renewContract).trigger('change').prop("disabled",true);
    $('.field-project__status', $formActionProject).val(project.status).trigger('change').prop("disabled",true);
    $('.field-project__technicalSkill', $formActionProject).val(project.technicalSkill).trigger('change').prop("disabled",true);

    $modalActionProject.modal('show');
}
function handleDefaultFormActionProject($formActionProject,contractTypes,customers,ranks,statuses,userRoleQA,userRolePms) {
    $formActionProject.validate().resetForm();
    $formActionProject.find('.form-control__item').removeClass('error');
    $formActionProject.find('#startDate-error-minDate').hide();
    buildContractTypeField($('.field-project__contract-type', $formActionProject),contractTypes);
    buildCustomerField($formActionProject,customers,ranks,statuses);
    buildUserFieldFormActionProject($formActionProject,userRoleQA,userRolePms);
}
function buildContractTypeField($selector,contractTypes) {
    $selector.empty();
    $selector.append('<option value="">-- Select --</option>');

    contractTypes.forEach(function (contractType) {
        $selector.append('<option value="' + contractType.id + '">' + contractType.name + '</option>');
    });
}
function buildCustomerField($formActionProject,customers,ranks,statuses) {
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
function buildUserFieldFormActionProject($formActionProject,userRoleQA,userRolePms) {
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
