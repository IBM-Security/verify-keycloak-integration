<#import "template.ftl" as layout>
<@layout.registrationLayout showAnotherWayIfPresent=false; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        ${msg("loginTitleHtml",realm.name)}
    <#elseif section = "form">
        <form id="kc-totp-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="totp" class="${properties.kcLabelClass!}">Select how you'd like to receive an OTP:</label>
                </div>

                <div class="${properties.kcInputWrapperClass!}">
					<!-- The enumeration of OTP methods should be dynamic based on what's available for the user. -->
                	<div style="display: flex; flex-direction: row; align-items: center;">
                    	<input id="email" name="otpType" type="radio" class="${properties.kcInputClass!}" value="email" style="display: inline; width: unset"/>
                    	<label for="email" style="margin-bottom: 0;">Email</label>
                    </div>
                    <div style="display: flex; flex-direction: row; align-items: center;">
	                    <input id="sms" name="otpType" type="radio" class="${properties.kcInputClass!}" value="sms" style="display: inline; width: unset"/>
	                    <label for="sms" style="margin-bottom: 0;">SMS</label>
	                </div>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <div class="${properties.kcFormButtonsWrapperClass!}">
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}" name="cancel" id="kc-cancel" type="submit" value="${msg("doCancel")}"/>
                    </div>
                </div>
            </div>
        </form>
        <#if client?? && client.baseUrl?has_content>
            <p><a id="backToApplication" href="${client.baseUrl}">${msg("backToApplication")}</a></p>
        </#if>
    </#if>
</@layout.registrationLayout>
