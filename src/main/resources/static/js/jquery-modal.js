$.fn.modalConfirm = function (options) {
  var self = this;

  self.find('.modal-title').text(options.title);
  self.find('.message').text(options.message);
  self.find('.btnConfirmOK').unbind('click');
  self.find('.btnConfirmOK').on('click', function() {
    self.modal('hide');
    if (options.confirmed) options.confirmed();
  });

  self.modal('show');
}

$.fn.modalMessage = function (options) {
  var self = this;
  if (self.hasClass('show')) {
    self.modal('hide');
  }
  self.find('.modal-title').text(options.title || 'Notification');
  self.find('.message').text(options.message);
  self.modal('show');

  setTimeout(function() {
    self.modal('hide');
  }, 60000);
}