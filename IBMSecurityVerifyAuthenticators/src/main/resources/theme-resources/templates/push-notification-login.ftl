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
        <div align="center">
            <form id="kc-qr-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">${msg("pushNotificationFormLoginSentMessage")}</div>
                </div>
                <input type="hidden" name="action" id="action-input" value="authenticate"/>
            </form>
        </div>
        <script type="text/javascript">
			// Form and action input to specify the operation on page submit.
        	var form = document.getElementById('kc-qr-login-form');
            var actionInput = document.getElementById('action-input');
            var pushImg = document.createElement('img');
        	pushImg.src = '${url.resourcesPath}/img/ibm_security_logo.png';
        	pushImg.style['max-width'] = '100%';
        	pushImg.style['height'] = 'auto';
        	form.appendChild(pushImg);

			// Poll in 5 seconds to see if the push notification was completed.
        	setTimeout(function() {
	        	form.submit();
        	}, 5000);
        </script>
    </#if>
</@layout.registrationLayout>
