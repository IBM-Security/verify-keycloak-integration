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
                    <div class="${properties.kcLabelWrapperClass!}">${msg("pushNotificationLoginSentMessage")}</div>
                </div>
                <input type="hidden" name="action" id="action-input" value="authenticate"/>
            </form>
        </div>
        <script type="text/javascript">
			// Form and action input to specify the operation on page submit.
        	var form = document.getElementById('kc-qr-login-form');
            var actionInput = document.getElementById('action-input');

			// Poll in 5 seconds to see if the push notification was completed.
        	setTimeout(function() {
	        	form.submit();
        	}, 5000);
        </script>
    </#if>
</@layout.registrationLayout>
