$(function () {
	const MSG_SUCCESS = "success";
	function handleErrorApi(error) {
		MODAL.message({
			message: error.message || 'Request failed.'
		});
		hideLoading();
	}

	/* Regex */
	function cm_isDate(date) {
		var isDate = Date.parse(date);

		if (isNaN(isDate) || !/^[0-9]+[-/][0-9]{2}[-/][0-9]{2}$/.test(date)) {
			return 'Invalid date format [yyyy/mm/dd, yyyy-mm-dd].';
		}

		return MSG_SUCCESS;
	}

	function buildModelRequestBody($fields) {
		var modelSearch = {};

		$.each($fields, function (key, field) {
			var $field = $(field);
			var fieldName = $field.data('field');
			var value = $field.data('type') === 'number' ? ($field.val() ? Number($field.val()) : $field.val()) : $field.val();

			if (fieldName) {
				if ($field.attr('type') === 'checkbox') {
					modelSearch[fieldName] = modelSearch[fieldName] || [];

					if ($field.is(":checked")) {
						modelSearch[fieldName].push(value);
					}
				} else if ($field.attr('type') === 'radio') {
					if ($field.is(':checked')) {
						modelSearch[fieldName] = value;
					}
				} else {
					modelSearch[fieldName] = value;
				}
			}
		});

		return modelSearch;
	}

	function buildEmptyModelRequestBodyEmployees($fields) {
		var modelSearch = {};

		$.each($fields, function (key, field) {
			var $field = $(field);
			var fieldName = $field.data('field');

			if (fieldName) {
				if($field.attr('name') === 'status'){
					modelSearch[fieldName] = ['1'];
				}
				else if ($field.attr('type') === 'checkbox') {
					modelSearch[fieldName] = [];
				} else
					modelSearch[fieldName] = '';
			}
		});

		return modelSearch;
	}

	function buildEmptyModelRequestBodyCustomers($fields) {
		var modelSearch = {};

		$.each($fields, function (key, field) {
			var $field = $(field);
			var fieldName = $field.data('field');

			if (fieldName) {
				if($field.attr('name') === 'status'){
					modelSearch[fieldName] = ['1'];
				}
				else if ($field.attr('type') === 'checkbox') {
					modelSearch[fieldName] = [];
				} else
					modelSearch[fieldName] = '';
			}
		});

		return modelSearch;
	}

	function buildEmptyModelRequestBodyProjects($fields) {
		var modelSearch = {};

		$.each($fields, function (key, field) {
			var $field = $(field);
			var fieldName = $field.data('field');
				modelSearch[fieldName] = '';
		});

		return modelSearch;
	}

	function buildEmptyModelRequestBodyOpportunities($fields) {
		var modelSearch = {};

		$.each($fields, function (key, field) {
			var $field = $(field);
			var fieldName = $field.data('field');

			if (fieldName) {
				if($field.attr('name') === 'status'){
					modelSearch[fieldName] = ['1', '2', '3'];
				}else{
					modelSearch[fieldName] = '';
				}
			}
		});

		return modelSearch;
	}

	function buildEmptyModelRequestBodyAllocations($fields) {
		var modelSearch = {};

		$.each($fields, function (key, field) {
			var $field = $(field);
			var fieldName = $field.data('field');

			if (fieldName) {
				if ($field.attr('type') === 'checkbox') {
					modelSearch[fieldName] = [];
				} else
					modelSearch[fieldName] = '';
			}
		});

		return modelSearch;
	}

	function commonApi(url, method, option) {
		var $defe = $.Deferred();
		var token = $("meta[name='_csrf']").attr("content");

		var ajaxOptions = {
			url: url,
			type: method,
			cache: false,
			contentType: 'application/json; charset=utf-8',
			success: function (result) {
				$defe.resolve(result);
			},
			error: function (xhr, status, error) {
				$defe.reject(xhr.responseJSON);
				handleErrorApi('Error ' + error);
			}
		};

		if (method !== 'GET') {
			ajaxOptions = $.extend(ajaxOptions, option);
		}

		$.ajax(ajaxOptions);

		return $defe.promise();
	}

	function postApi(url, jsonObject, method) {
		var options = {
			headers: {
				'X-CSRF-TOKEN': $('._csrf').val()
			}
		}
		if (jsonObject) options.data = JSON.stringify(jsonObject);

		return commonApi(url, method, options);
	}

	function getApi(url) {
		return commonApi(url, 'GET');
	}

	function showLoading() {
		$('body').prepend('<div class="cover-loading"></div>');
		$('body').prepend('<div class="loading"></div>');
	};

	//Hide loading
	function hideLoading() {
		$('body div.cover-loading').remove();
		$('body div.loading').remove();
	};

	function formatDateToString(date) {
		if (date && new Date(date) !== 'Invalid date' && !isNaN(new Date(date))) {
			return new Date(date).toLocaleDateString('en-GB')
		}

		return '';
	}

	function formatStringToDate(str) {
		if (str && new Date(str) !== 'Invalid date' && !isNaN(new Date(str))) {
			return new Date(str);
		}

		return null;
	}

	function findUserByUserId(users, userId) {
		var user = users.filter(function (user) {
			return user.id === Number(userId);
		});

		return user && user[0] || {};
	}

	function getUserNameByUserId(users, userId) {
		var user = findUserByUserId(users, userId);

		return user.employeeName || '';
	}

	function getCustomTypeFromAppParams(appParams, type) {
		return appParams.filter(function (item) {
			return item.type === type;
		});
	}

	function getContractNameByContractId(contractTypes, contractTypeId) {
		var contract = contractTypes.filter(function (contract) {
			return contract.id === Number(contractTypeId);
		});

		return contract[0] && contract[0].name || '';
	}

	function getRankNameByRankId(ranks, rankId) {
		var rank = ranks.filter(function (rank) {
			return rank.id === Number(rankId);
		});

		return rank[0] && rank[0].name || '';
	}

	function getCustomerNameByCustomerId(customers, customerId) {
		var customer = customers.filter(function (customer) {
			return customer.id === Number(customerId);
		});

		return customer[0] && customer[0].customerName || '';
	}

	function startOfWeek(date) {
		var diff = date.getDate() - date.getDay() + (date.getDay() === 0 ? -6 : 1);

		return new Date(date.setDate(diff));
	}

	Date.prototype.getTotalDaysOfMonth = function () {
		return new Date(this.getFullYear(), this.getMonth() + 1, 0).getDate();
	}

	Date.prototype.addDays = function (days) {
		this.setDate(this.getDate() + parseInt(days));

		return this;
	};

	Date.prototype.minusDays = function (days) {
		this.setDate(this.getDate() - parseInt(days));

		return this;
	};

	Date.prototype.clone = function () {
		return new Date(this.getTime());
	}

	//Custom format date time picker
	if ($.fn.datepicker) {
		$.fn.datepicker.defaults.format = 'dd/mm/yyyy';
	}

	//Custom validate
	$.validator.addMethod('pattern', function (value, element, pattern) {
		return pattern.test(value);
	}, 'Please enter valid pattern');

	$.extend($.validator.messages, {
		email: 'Please enter valid email format.',
		date: 'Please enter valid date format [yyyy/mm/dd, yyyy-mm-dd].',
		number: 'Please enter number.',
		maxlength: $.validator.format('Please enter no more than {0} characters.'),
		minlength: $.validator.format('Please enter more than {0} characters.'),
		range: $.validator.format('Please enter a number between {0} and {1}.'),
		max: $.validator.format('Please enter a number no greater than {0}.'),
		min: $.validator.format('Please enter a number greater than {0}.')
	});

	$.validator.setDefaults({
		errorPlacement: function (error, element) {
			if (element.parent().hasClass('input-group')) {
				error.insertAfter(element.parent());
			} else if (element.hasClass('combobox')) {
				if (element.closest('.form-group').find('.input-group').length) {
					error.insertAfter(element.closest('.form-group'));
				} else {
					var $comboboxInput = element.closest('.form-group').find('.form-control-combobox-input');

					$comboboxInput.removeClass('valid');
					$comboboxInput.addClass('error');

					error.insertAfter(element.closest('.form-group').find('.form-control-combobox'));
				}
			} else {
				error.insertAfter(element);
			}
		},
		ignore: ':disabled,.ignore'
	});

	const MESSAGE = {
		REQUIRED: 'Please enter {0}.',
		CHAR0TO200: 'Please enter characters and numbers (a-z A-z 0-9) and no more than 200 characters.',
		DATE: 'Please enter valid date format [yyyy/mm/dd, yyyy-mm-dd].',
		getMessageFormat: function (message, prefix) {
			return $.validator.format(message, prefix)
		},
		getMessageRequired: function (prefix) {
			return this.getMessageFormat(this.REQUIRED, prefix)
		}
	}

	const PATTERN = {
		DATE_FORMAT: /(\d{2})\/(\d{2})\/(\d{4})/,
		END_LINE: /[\u21b5\r\n]/g
	}

	const MODAL = {
		confirm: function (options) {
			$('#dialog-common-confirm').modalConfirm(options);
		},
		message: function (options) {
			$('#dialog-common-message').modalMessage(options);
		}
	}

	$.widget('custom.combobox', {
		_create: function () {
			this.wrapper = $('<span>')
				.addClass('form-control-combobox')
				.insertAfter(this.element);

			this.element.hide();
			this.element.addClass('combobox');
			this._createAutocomplete();
		},

		_createAutocomplete: function () {
			var self = this;
			var selected = self.element.val() ?
				self.element.children('[value="' + self.element.val() + '"]') :
				self.element.children(':selected');
			var value = selected.val() ? selected.text() : '';

			function validate() {
				self._delay(function () {
					var isValid = $(self.element).valid();

					self.input.toggleClass('error', !isValid);
					self.input.toggleClass('valid', isValid);
				});
			}

			self.input = $('<input>')
				.appendTo(self.wrapper)
				.val(value)
				.attr('name', self.element.attr('name') + '_combobox')
				.attr('placeholder', (self.element.attr('placeholder') || '-- Select --'))
				.addClass('form-control form-control-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left')
				.autocomplete({
					delay: 0,
					minLength: 0,
					classes: {
						'ui-autocomplete': 'ui-combobox'
					},
					source: $.proxy(self, '_source')
				});

			self._on(self.input, {
				autocompleteselect: function (event, ui) {
					ui.item.option.selected = true;
					$(self.element).valid();
					self._trigger('select', event, {
						item: ui.item.option
					});
				},

				autocompletechange: '_removeIfInvalid'
			});

			$(self.element).on('change', function () {
				var selected = self.element.val() ?
					self.element.children('[value="' + self.element.val() + '"]') :
					self.element.children(':selected');
				var value = selected.val() ? selected.text() : '';

				self.input.val(value);
			});

			$(self.input).on('focusout', validate);
			$(self.input).on('focus', function () {
				$(this).autocomplete('search', '');
			});
		},

		_source: function (request, response) {
			var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), 'i');
			response(this.element.children('option').map(function () {
				var text = $(this).text();

				if (this.value && (!request.term || matcher.test(text)))
					return {
						label: text,
						value: text,
						option: this
					};
			}));
		},

		_removeIfInvalid: function (event, ui) {
			// Selected an item, nothing to do
			if (ui.item) {
				return;
			}

			// Search for a match (case-insensitive)
			var value = this.input.val(),
				valueLowerCase = value.toLowerCase(),
				valid = false;
			this.element.children('option').each(function () {
				if ($(this).text().toLowerCase() === valueLowerCase) {
					this.selected = valid = true;
					return false;
				}
			});

			this.element.valid();

			// Found a match, nothing to do
			if (valid) {
				return;
			}

			// Remove invalid value
			this.input.val('')
			this.element.val('');

			this._delay(function () {
				this.element.valid();
			}, 2500);

			this.input.autocomplete('instance').term = '';
		},

		_destroy: function () {
			this.wrapper.remove();
			this.element.show();
		}
	});

	var fixedScrollBar = (function () {
		var $scrollbar = $('<div id="fixed-scrollbar" class="fixed-scrollbar-global"><div></div></div>').appendTo($(document.body));
		var $fakeContent = $scrollbar.find('div');
		var $active = $([]);

		function top(e) { return e.offset().top; }
		function bottom(e) { return e.offset().top + e.height(); }

		function onscroll() {
			var oldactive = $active;
			$active = findScrollActive();

			if (oldactive.not($active).length) {
				oldactive.unbind('scroll', update);
			}

			if ($active.not(oldactive).length) {
				$active.scroll(update);
			}

			update();
		}

		function findScrollActive() {
			$scrollbar.show();

			var $active = $([]);

			$('.fixed-scrollbar').each(function () {
				if (top($(this)) < top($scrollbar) && bottom($(this)) > bottom($scrollbar)) {
					$fakeContent.width($(this).get(0).scrollWidth);
					$fakeContent.height(1);
					$active = $(this);
				}
			});

			fit($active);

			return $active;
		}

		function fit(active) {
			if (!active.length) return $scrollbar.hide();

			$scrollbar.css({ left: active.offset().left, width: active.width() });
			$fakeContent.width($(this).get(0).scrollWidth);
			$fakeContent.height(1);

			delete lastScroll;
		}

		var lastScroll;

		function scroll() {
			if (!$active.length) return;
			if ($scrollbar.scrollLeft() === lastScroll) return;

			lastScroll = $scrollbar.scrollLeft();
			$active.scrollLeft(lastScroll);
		}

		function update() {
			if (!$active.length) return;
			if ($active.scrollLeft() === lastScroll) return;

			lastScroll = $active.scrollLeft();
			$scrollbar.scrollLeft(lastScroll);
		}

		$scrollbar.scroll(scroll);

		$(window).scroll(onscroll);
		$(window).resize(onscroll);

		onscroll();

		return {
			$scrollbar: $scrollbar,
			$active: $active,
			reload: onscroll,
		};
	})();

	$(function () {
		$('#btn-logout').click(function (e) {
			e.preventDefault();
			$('#form-logout').submit();
		})});

	var CommonUtils = {
		postApi: postApi,
		getApi: getApi,
		buildModelRequestBody: buildModelRequestBody,
		buildEmptyModelRequestBodyEmployees: buildEmptyModelRequestBodyEmployees,
		buildEmptyModelRequestBodyCustomers: buildEmptyModelRequestBodyCustomers,
		buildEmptyModelRequestBodyProjects: buildEmptyModelRequestBodyProjects,
		buildEmptyModelRequestBodyAllocations :buildEmptyModelRequestBodyAllocations,
		buildEmptyModelRequestBodyOpportunities :buildEmptyModelRequestBodyOpportunities,
		hideLoading: hideLoading,
		showLoading: showLoading,
		formatDateToString: formatDateToString,
		formatStringToDate: formatStringToDate,
		getUserNameByUserId: getUserNameByUserId,
		findUserByUserId: findUserByUserId,
		getContractNameByContractId: getContractNameByContractId,
		getCustomTypeFromAppParams: getCustomTypeFromAppParams,
		getRankNameByRankId: getRankNameByRankId,
		getCustomerNameByCustomerId: getCustomerNameByCustomerId,
		startOfWeek: startOfWeek,
		fixedScrollBar: fixedScrollBar,
	};

	window.CommonUtils = CommonUtils;
	window.CommonMessage = MESSAGE;
	window.CommonPattern = PATTERN;
	window.CommonModal = MODAL;
});