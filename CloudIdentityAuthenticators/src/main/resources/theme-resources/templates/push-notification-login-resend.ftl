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
                    <div class="${properties.kcLabelWrapperClass!}">${msg("pushNotificationFormResendMessage")}</div>
                </div>
                <input type="hidden" name="action" id="action-input"/>
            </form>
            <button id="resend-required" class="btn btn-primary btn-block btn-lg">
                ${msg("pushNotificationFormResendButton")}
            </button>
        </div>
        <script type="text/javascript">
			// Form and action input to specify the operation on page submit.
        	var form = document.getElementById('kc-qr-login-form');
            var actionInput = document.getElementById('action-input');
            
            var resendBtn = document.getElementById('resend-required');
            if (resendBtn) {
                resendBtn.addEventListener('click', (event) => {
                    actionInput.value = 'resend';
                    form.submit();
                });
            }
        </script>
    </#if>
</@layout.registrationLayout>
