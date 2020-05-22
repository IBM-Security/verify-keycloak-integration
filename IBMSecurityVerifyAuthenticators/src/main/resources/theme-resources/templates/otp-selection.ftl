/*
    Copyright 2020 IBM
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
      http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

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
                    <label for="totp" class="${properties.kcLabelClass!}">${msg("otpSelectionFormMsg")}</label>
                </div>

                <div class="${properties.kcInputWrapperClass!}">
					<!-- The enumeration of OTP methods should be dynamic based on what's available for the user. -->
                	<div style="display: flex; flex-direction: row; align-items: center;">
                        <input id="email" name="otpType" type="radio" class="${properties.kcInputClass!}" value="email" style="display: inline; width: unset"/ checked>
                        <label for="email" style="margin-bottom: 0; text-indent: 5px">${msg("otpSelectionFormEmail")} ${otpEmailHint}</label>
                    </div>
                    <div style="display: flex; flex-direction: row; align-items: center;">
	                    <input id="sms" name="otpType" type="radio" class="${properties.kcInputClass!}" value="sms" style="display: inline; width: unset"/>
	                    <label for="sms" style="margin-bottom: 0; text-indent: 5px">${msg("otpSelectionFormSms")} ${otpSmsHint}</label>
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
                        <input class="btn btn-primary btn-block btn-lg" name="login" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                    </div>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
