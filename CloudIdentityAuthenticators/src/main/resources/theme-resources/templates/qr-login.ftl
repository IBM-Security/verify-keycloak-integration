<#import "template.ftl" as layout>
<@layout.registrationLayout showAnotherWayIfPresent=false; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        ${msg("loginTitleHtml",realm.name)}
    <#elseif section = "form">
        <div align="center">
            <form id="kc-qr-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">${msg("qrFormMessage")}</div>
                </div>
                <input type="hidden" name="action" id="action-input" value="authenticate"/>
            </form>
            <button id="registration-required" class="btn btn-primary btn-block btn-lg">
                ${msg("verifyFormRegisterButton")}
            </button>
        </div>
        <script type="text/javascript">
			// Form and action input to specify the operation on page submit.
        	var form = document.getElementById('kc-qr-login-form');
            var actionInput = document.getElementById('action-input');
        	var qrCodeImg = document.createElement('img');
        	var qrSrc = '${qrCode}';
        	qrCodeImg.src = 'data:image/png;base64,' + qrSrc;
        	form.appendChild(qrCodeImg);

            var regBtn = document.getElementById('registration-required');
            if (regBtn) {
                regBtn.addEventListener('click', (event) => {
                    actionInput.value = 'register';
                    form.submit();
                });
            }

			// Poll in 5 seconds to see if the login is completed.
        	setTimeout(function() {
	        	form.submit();
        	}, 5000);

        </script>
    </#if>
</@layout.registrationLayout>
