<#--
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
-->

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
	                </div>
	            </div>
	        </form>
	    </div>
    </#if>
</@layout.registrationLayout>
