<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        ${msg("loginTitleHtml",realm.name)}
    <#elseif section = "form">
        <form id="kc-qr-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">Scan this QR Code with your IBM Verify mobile application.</div>
                <div class="${properties.kcLabelWrapperClass!}">Once scanned, your IBM Verify registration will be complete.</div>
            </div>
			<input type="hidden" name="action" id="action-input" value="register"/>
        </form>
        <script type="text/javascript">
			// Form and action input to specify the operation on page submit.
        	var form = document.getElementById('kc-qr-login-form');
			var actionInput = document.getElementById('action-input');
        	var qrCodeImg = document.createElement('img');
        	var qrSrc = '${qrCode}';
        	qrCodeImg.src = 'data:image/png;base64,' + qrSrc;
        	form.appendChild(qrCodeImg);

			// Poll every 5 seconds to see if registration has been completed.
        	setTimeout(function() {
	        	form.submit();
        	}, 5000);

        </script>
        <#if client?? && client.baseUrl?has_content>
            <p><a id="backToApplication" href="${client.baseUrl}">${msg("backToApplication")}</a></p>
        </#if>
    </#if>
</@layout.registrationLayout>
