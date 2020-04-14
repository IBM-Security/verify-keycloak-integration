<#import "template.ftl" as layout>
<@layout.registrationLayout showAnotherWayIfPresent=false; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        ${msg("loginTitleHtml",realm.name)}
    <#elseif section = "form">
    	<div align="center">
	        <form id="kc-totp-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
	        	<input id="otpCorrelation" name="${otpCorrelationFormName}" type="hidden" value="${otpCorrelationValue}"/>
	            <div class="${properties.kcFormGroupClass!}">
	                <div class="${properties.kcLabelWrapperClass!} align="left">
	                    <label for="totp" class="${properties.kcLabelClass!}">${otpTypeLabel}</label>
	                </div>
	
	                <div class="${properties.kcInputWrapperClass!}" style="display: flex; flex-direction: row; align-items: center;">
	                    <div style="font-size: 12px; padding-right: 0.5rem;">${otpCorrelationValue}-</div><input id="totp" name="${otpFormName}" type="text" class="${properties.kcInputClass!}" />
	                </div>
	            </div>

	            <div class="${properties.kcFormGroupClass!}">
	                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
	                    <div class="${properties.kcFormOptionsWrapperClass!}">
	                    </div>
	                </div>
	
	                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
	                    <input class="btn btn-primary btn-block btn-lg" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
	                    <input class="btn btn-default btn-block btn-lg" name="cancel" id="kc-cancel" type="submit" value="${msg("doCancel")}"/>
	                </div>
	            </div>
	        </form>
	    </div>
    </#if>
</@layout.registrationLayout>
