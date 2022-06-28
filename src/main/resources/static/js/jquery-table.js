$(function () {
  function DataTable(options, $control) {
    if (!(this instanceof DataTable)) {
      return new DataTable(options, $control);
    }

    var defaultOptions = {
      itemsCore: [],
      items: [],
      showDefaultAction: false,
      showColumnNo: false,
      showColumnChooser: false,
      showPaging: true,
      actions: [],
      page: 1,
      limit: 50,
      totalItems: undefined,
      pagingByClient: true,
      sortByClient: false,
      limitOptions: [10, 25, 50, 100],
      sortOptions: {},
      fixedColumns: {
        leftColumns: 0,
        rightColumns: 0, // TODO: Make table can fixed right columns
      }
    };

    this.options = $.extend(defaultOptions, options);
    this.$control = $control;
    this.initTable();

    return this;
  }

  var tableControl = DataTable.prototype;
  tableControl.initTable = function () {
    this.$control.wrap('<div class="table-wrapper"></div>');
    this.$control.wrap('<div class="table-wrapper-scroll fixed-scrollbar"></div>');

    this.$controlWrapper = this.$control.closest('div.table-wrapper');
    this.$controlWrapperScroll = this.$control.closest('div.table-wrapper-scroll');

    this.buildHeader();
  }

  tableControl.hasFixedColumns = function () {
    return this.options.fixedColumns && (this.options.fixedColumns.leftColumns || this.options.fixedColumns.rightColumns);
  };

  tableControl.buildFixedColumns = function () {
    var self = this;
    window.self = self;

    self.$fixedColumnsTable = self.$control.clone(true);
    self.$fixedColumnsTable.addClass('table-freeze');
    self.$fixedColumnsTable.attr('id', self.$fixedColumnsTable.attr('id') + '_freezeTable');

    if (self.$controlWrapper.find('.table-freeze').length) {
      self.$controlWrapper.find('.table-freeze').replaceWith(self.$fixedColumnsTable);
    } else {
      self.$controlWrapper.append(self.$fixedColumnsTable);
    }

    if (this.options.fixedColumns.leftColumns) {
      var size = this.options.fixedColumns.leftColumns;
      var leftColumnsSelector = '';
      var lastLeftColumnsSelector = 'tr td:nth-child(' + size + '), tr th:nth-child(' + size + ')';

      for (let i = 1; i <= size; i++) {
        if (i > 1) {
          leftColumnsSelector += ',';
        }

        leftColumnsSelector += 'tr td:nth-child(' + i + '), tr th:nth-child(' + i + ')';
      }

      self.$fixedColumnsTable.find('.cell-freeze, .cell-freeze-left, .cell-freeze-left-last')
        .removeClass('cell-freeze cell-freeze-left cell-freeze-left-last');
      self.$fixedColumnsTable.find(leftColumnsSelector).addClass('cell-freeze cell-freeze-left');
      self.$fixedColumnsTable.find(lastLeftColumnsSelector).addClass('cell-freeze-left-last');
    }

    CommonUtils.fixedScrollBar.reload();
  };

  tableControl.buildHeader = function () {
    var self = this;
    var $header = self.$control.find('thead tr');

    if ($header.length) {
      $header.empty();
    } else {
      $header = $('<thead></thead>').append($('<tr></tr>').addClass('table-header'));
      self.$control.append($header);
    }

    var $controlHeader = self.$control.find('thead tr');

    if (self.options.showColumnNo) {
      $controlHeader.append($('<th></th>').text('No'));
    }

    self.options.fields.forEach(function (field) {
      var $th = $('<th></th>').addClass('table-column');

      if (field.headerWidth) {
        $th.css({ 'min-width': field.headerWidth });
      }

      $controlHeader.append($th.text(field.name));

      if (field.canBeSort) {
        $th.addClass('hover-pointer');
        var $sorting = $('<span></span>').addClass('sorting ml-2');
        var $sortDesc = $('<i></i>').addClass('glyphicon glyphicon-triangle-bottom sorting-option');
        var $sortAsc = $('<i></i>').addClass('glyphicon glyphicon-triangle-top sorting-option');
        var sortOptions = self.options.sortOptions;
        var fieldSort = field.sortField || field.valueKey;

        $sortDesc.toggleClass('active', sortOptions.field === fieldSort && sortOptions.sortType.toUpperCase() === 'DESC');
        $sortAsc.toggleClass('active', sortOptions.field === fieldSort && sortOptions.sortType.toUpperCase() === 'ACS');

        var changeSort = function (sortType) {
          $header.find('.table-column .sorting-option').removeClass('active');
          var $selector = sortType === 'DESC' ? $sortDesc : $sortAsc;

          $selector.addClass('active');
          self.setSortOptions({
            field: fieldSort,
            sortType: sortType
          });

          self.setPage(1);
          if (self.options.sortByClient) {
            self.setItemsCore();
          } else if (self.options.fetch) {
            self.options.fetch();
          }
        }

        $th.on('click', function () {
          var sortType = self.options.sortOptions.field === fieldSort ?
            (self.options.sortOptions.sortType.toUpperCase() === 'DESC' ? 'ACS' : 'DESC') : 'ACS';

          changeSort(sortType);
        });
        $sorting.append($sortAsc);
        $sorting.append($sortDesc);
        $th.append($sorting);
      }
    });

    if (self.options.showDefaultAction || self.options.actions.length) {
      $controlHeader.append($('<th></th>').addClass('column-action').text('Action'));
    }

    if (self.options.newHeaders && self.options.newHeaders.length) {
      self.options.newHeaders.forEach(function (field) {
        var $th = $('<th></th>').addClass('table-column');
        $th.text(field.label);

        $controlHeader.append($th);
      });
    }

    if (self.hasFixedColumns()) {
      self.buildFixedColumns();
    }
  }

  tableControl.sortTable = function (items) {
    var self = this;
    var sortOptions = self.options.sortOptions;
    var itemsTemp = $.extend([], items);

    if (sortOptions.field) {
      itemsTemp.sort(function (current, next) {
        var isSortDesc = sortOptions.sortType && sortOptions.sortType.toUpperCase() === 'DESC';

        if (typeof next[sortOptions.field] === 'number' && typeof current[sortOptions.field] === 'number') {
          if (isSortDesc) return next[sortOptions.field] - current[sortOptions.field];
          return current[sortOptions.field] - next[sortOptions.field];
        } else if (isSortDesc) {
          return next[sortOptions.field].toString().localeCompare(current[sortOptions.field].toString());
        }

        return current[sortOptions.field].toString().localeCompare(next[sortOptions.field].toString());
      });
    }

    return $.makeArray(itemsTemp);
  }

  tableControl.buildContent = function () {
    var self = this;
    var $body = $('<tbody></tbody>');
    var $controlBody = self.$control.find('tbody');

    if ($controlBody.length) {
      $controlBody.empty();
    } else {
      self.$control.append($body);
      $controlBody = $body;
    }

    self.options.items.forEach(function (item, key) {
      var $controlRow = $('<tr></tr>');
      $controlBody.append($controlRow);
      $controlRow.data('item', item);

      if(self.options.getRowColor) {
        $controlRow.css('color', self.options.getRowColor(item));
      }

      if (self.options.showColumnNo) {
        $controlRow.append($('<td></td>').text(self.getColumnNoIndex(key)));
      }

      self.options.fields.forEach(function (field) {
        var method = field.isTextArea ? 'append' : 'text';
        var value = field.isTextArea ? (item[field.valueKey] || '').replace(CommonPattern.END_LINE, '<br />') : item[field.valueKey];
        var $cell = $('<td></td>');

        $cell.attr('data-field', field.valueKey || field.name);
        $cell[method](value || '-');

        if (field.click) {
          $cell.on('click', function (event) {
            field.click(event, item, self);
          });
        }

        if (field.class) {
          $cell.addClass(field.class);
        }

        $controlRow.append($cell);
      });

      if (self.options.showDefaultAction || self.options.actions.length) {
        var $cell = $('<td class="cell-actions"></td>');

        $cell.append(self.buildAction('glyphicon-edit', self.options.onEditItem, item));
        $cell.append(self.buildAction('glyphicon-trash', self.options.onDeleteItem, item));
        // $cell.append(self.buildAction('glyphicon-menu-hamburger', self.options.onViewItem, item));
        self.options.actions.forEach(function (action) {
          $cell.append(self.buildAction(action.icon, action.funcAction, item));
        })

        $controlRow.append($cell);
      }

      if (self.options.newHeaders && self.options.newHeaders.length) {
        self.options.newHeaders.forEach(function (header) {
          var value = self.options.getValueForCell && self.options.getValueForCell(header, item);
          var $td = $('<td></td>').append(value);

          $controlRow.append($td);
        })
      }
    });

    if (self.hasFixedColumns()) {
      self.buildFixedColumns();
    }
  }

  tableControl.getColumnNoIndex = function (key) {
    if (this.options.showPaging) {
      return (this.options.limit * (this.options.page - 1)) + key + 1;
    }

    return key + 1;
  }

  tableControl.buildAction = function (className, funcAction, item) {
    var $icon = $('<i></i>');

    $icon.addClass('glyphicon mr-2 pointer').addClass(className);

    if (funcAction) {
      $icon.on('click', funcAction.bind(this, item));
    }

    return $icon;
  }

  tableControl.setItemsCore = function (items) {
    this.options.itemsCore = items || this.options.itemsCore;
    if (this.options.sortByClient) {
      this.options.itemsCore = this.sortTable(items || this.options.itemsCore);
    }

    if (this.options.showPaging && this.options.pagingByClient) {
      this.setItemsForPage();
    } else {
      this.setItems(itemsCore);
    }
  }

  tableControl.setItems = function (items) {
    this.options.items = items || this.options.items;

    if (this.options.handleChangeItem) {
      this.options.handleChangeItem(this.options.items).then(this.changeItems.bind(this));
    } else {
      this.changeItems();
    }

    if (this.options.showPaging) {
      this.buildPaging();
    }
  }

  tableControl.changeItems = function () {
    if (this.options.newHeaders && this.options.newHeaders.length) {
      this.buildHeader();
    }

    this.buildContent();
  }

  tableControl.setItemsForPage = function () {
    var startIndex = (this.options.page - 1) * this.options.limit;
    var endIndex = this.options.page * this.options.limit
    var items = this.options.itemsCore.slice(startIndex, endIndex);

    this.setItems(items);
  }

  tableControl.buildPaging = function () {
    var self = this;
    var $paging = $('<div class="paging"></div>');
    var $pagingItems = $('<ul></ul>').addClass('paging-list');
    $paging.append($pagingItems);

    var $controlPaging = self.$control.closest('.table-wrapper').find('.paging');

    if ($controlPaging.length) {
      $controlPaging.empty();
      $controlPaging.append($pagingItems);
    } else {
      self.$control.closest('.table-wrapper-scroll').after($paging);
    }

    var currentPage = self.options.page;
    var totalItems = self.options.totalItems || self.options.itemsCore.length;
    var maxPage = Math.ceil(totalItems / self.options.limit);
    var startPage = currentPage > 2 ? currentPage - 2 : 1;
    var endPage = currentPage + 2 < maxPage ? currentPage + 2 : maxPage;
    var displayTotalItem = buildFormDisplayTotalItem(totalItems, self.options.limit, currentPage);

    if (currentPage === 1 || currentPage === 2) {
      if (maxPage >= 5) endPage = 5;
      if (maxPage < 5) endPage = maxPage;
    }

    if (currentPage === maxPage || currentPage === (maxPage - 1)) {
      if (maxPage < 5) startPage = 1;
      if (maxPage > 5) startPage = maxPage - 4;
    }

    $pagingItems.append(self.buildPagingItem('««', 1, currentPage === 1));
    $pagingItems.append(self.buildPagingItem('«', currentPage - 1, currentPage === 1));

    for (var i = startPage; i <= endPage; i++) {
      $pagingItems.append(self.buildPagingItem(i, i, currentPage === i));
    }

    $pagingItems.append(self.buildPagingItem('»', currentPage + 1, currentPage === maxPage));
    $pagingItems.append(self.buildPagingItem('»»', maxPage, currentPage === maxPage));
    $pagingItems.prepend(self.buildLimitOptions());
    $pagingItems.append($('<span></span>').text(''));
    $pagingItems.append($('<span class="display-total-item"></span>').text(displayTotalItem));
  }

  tableControl.buildPagingItem = function (text, page, isDisabled) {
    var $li = $('<li></li>').addClass('paging-item');
    var $button = $('<button></button>').text(text);

    if (page === this.options.page && text === page) {
      $li.addClass('active');
    }

    $button.on('click', this.onPaging.bind(this, page));
    $button.prop('disabled', isDisabled);

    $li.append($button);

    return $li;
  }

  tableControl.buildLimitOptions = function () {
    var self = this;
    var $selectLimit = $('<select></select>').addClass('form-control limit-paging mr-2');

    self.options.limitOptions.forEach(function (option) {
      $selectLimit.append('<option value="' + option + '">' + option + '</option>');
    });

    $selectLimit.val(self.options.limit);
    $selectLimit.on('change', function () {
      self.options.limit = $selectLimit.val();

      self.setPage(1);
      if (self.options.pagingByClient) {
        self.setItemsCore();
      } else if (self.options.fetch) {
        self.options.fetch();
      }
    })

    return $selectLimit;
  }

  tableControl.onPaging = function (page) {
    this.setPage(page);
    if (this.options.pagingByClient) {
      var startIndex = this.options.limit * (page - 1);
      var endIndex = this.options.limit * page;
      var itemsCoreTemp = $.extend([], this.options.itemsCore)
      var items = itemsCoreTemp.slice(startIndex, endIndex);

      this.setItems(items);
    } else if (this.options.fetch && (typeof this.options.fetch === 'function')) {
      this.options.fetch();
    }
  }

  tableControl.setPage = function (page) {
    this.options.page = page || 1;
  }

  tableControl.setSortOptions = function (sortOptions) {
    this.options.sortOptions = sortOptions || {};
  }

  tableControl.handlePageWhenDeleteItem = function () {
    if (this.options.pagingByClient && this.options.items.length === 1 && this.options.page > 1) {
      this.setPage(this.options.page - 1);
    }
  }

  tableControl.addNewHeader = function (newHeaders) {
    this.options.newHeaders = newHeaders;
    this.buildHeader();
    this.buildContent();
  }

  $.fn.dataTable = function (options) {
    return new DataTable(options, this);
  }

  function buildFormDisplayTotalItem(totalItems, limit, pageNumber) {
    var startItem = (totalItems > 0) ? (pageNumber - 1) * limit + 1 : 0;
    var endItem = (totalItems < pageNumber * limit) ? totalItems : pageNumber * limit;
    return 'Display ' + startItem + ' - ' + endItem + ' of ' + totalItems + ' records';
  }
});