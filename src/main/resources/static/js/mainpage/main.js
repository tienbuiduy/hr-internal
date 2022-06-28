$(function () {
    $(document).ready(intMenu);

    function intMenu() {
        CommonUtils.getApi('/api/functions/user-functions').then(function (data) {
            var a = $.map(data, function (o) {
                return `<li class="${location.pathname === o.path ? 'active' : ''}">
                        <div class="menu-item">
                            <a href="${o.path}" class="text-white">
                                <i class="${o.icon}" aria-hidden="true"></i>
                                ${o.functionName}
                            </a>
                        </div>
                    </li>`
            }).join("");
            $('#menu-left > ul').empty().append(a);
        });
    }
});

