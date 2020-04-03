<#import "template.ftl" as layout>
<@layout.registrationLayout showAnotherWayIfPresent=false; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        ${msg("loginTitleHtml",realm.name)}
    <#elseif section = "form">
        <form id="kc-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			<input id="action-type" type="hidden" name="action-type" value="bypass"/>
			<button id="user-pass-login">Login using username and password</button>
			<button id="start-authn">Login using FIDO2 device</button>
			<div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">Scan this QR Code with your IBM Verify mobile application.</div>
                <div class="${properties.kcLabelWrapperClass!}">Once scanned, your authentication will complete automatically.</div>
            </div>
        </form>
        <script type="text/javascript">
			// Various base 64 utilities used with FIDO operations
			const b64map = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
			const b64pad = '=';
			const alphanumeric = '0123456789abcdefghijklmnopqrstuvwxyz';
			const zero = 0;
			const one = 1;
			const two = 2;
			const three = 3;
			const four = 4;
			const six = 6;
			const fifteen = 15;
			const sixteen = 16;
			const sixtyThree = 63;

			function b64url(data) {
				return btoa(String.fromCharCode(...new Uint8Array(data))).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
			}

			function BAtoHex(collection) {
				let s = '';
				for (const a of collection) {
					let hex1 = a.toString(sixteen);
					if (hex1.length === one) {
						hex1 = '0' + hex1;
					}
					s = s + hex1;
				}
				return s;
			}

			function hextob64u(s) {
				let newS = s;
				if (newS.length % two === one) {
					newS = '0' + newS;
				}
				return b64tob64u(hex2b64(newS));
			}

			function hex2b64(h) {
				let i = 0;
				let c = 0;
				let ret = '';
				for (i = 0; i+three <= h.length; i+=three) {
					c = parseInt(h.substring(i, i+three), sixteen);
					ret += b64map.charAt(c >> six) + b64map.charAt(c & sixtyThree);
				}
				if (i+one === h.length) {
					c = parseInt(h.substring(i, i+one), sixteen);
					ret += b64map.charAt(c << two);
				} else if (i+two === h.length) {
					c = parseInt(h.substring(i, i+two), sixteen);
					ret += b64map.charAt(c >> two) + b64map.charAt((c & three) << four);
				}
				if (b64pad) {
					while ((ret.length & three) > zero) {
						ret += b64pad;
					}
				}
				return ret;
			}

			function b64tob64u(s) {
				let newS = s;
				newS = newS.replace(/\=/g, '');
				newS = newS.replace(/\+/g, '-');
				newS = newS.replace(/\//g, '_');
				return newS;
			}

			function b64utob64(s){
				let newS = s;
				if (newS.length % four === two) {
					newS = newS + '==';
				} else if (newS.length % four === three) {
					newS = newS + '=';
				}
				newS = newS.replace(/-/g, '+');
				newS = newS.replace(/_/g, '/');
				return newS;
			}

			function b64toBA(s) {
				const h = b64tohex(s);
				let i = 0;
				const a = new Array();
				for (i = 0; two*i < h.length; ++i) {
					a[i] = parseInt(h.substring(two*i, two*i+two), sixteen);
				}
				return a;
			}

			function b64tohex(s) {
				let ret = '';
				let i = 0;
				let k = 0; // b64 state, 0-3
				let slop = 0;
				let v = 0;
				for (i = 0; i < s.length; ++i) {
					if (s.charAt(i) === b64pad) { break; }
					v = b64map.indexOf(s.charAt(i));
					if (v < zero) { continue; }
					switch (k) {
						case zero:
							ret += int2char(v >> two);
							slop = v & three;
							k = one;
							break;
						case one:
							ret += int2char((slop << two) | (v >> four));
							slop = v & fifteen;
							k = two;
							break;
						case two:
							ret += int2char(slop);
							ret += int2char(v >> two);
							slop = v & three;
							k = three;
							break;
						default:
							ret += int2char((slop << two) | (v >> four));
							ret += int2char(v & fifteen);
							k = zero;
							break;
					}
				}
				if (k === 1) {
					ret += int2char(slop << two);
				}
				return ret;
			}

			function int2char(n) {
				return alphanumeric.charAt(n);
			}

			// End of base 64 utility functions used with FIDO operations

			// Form and associated action input element references
			var form = document.getElementById('kc-login-form');
			var actionInput = document.getElementById('action-type');

			// Username+password login button
			var userPassButton = document.getElementById('user-pass-login');
			if (userPassButton) {
				userPassButton.addEventListener('click', (event) => {
					event.preventDefault();
					actionInput.value = 'bypass';
					form.submit();
				});
			}

			// QR Code authentication
			var qrCodeImg = document.createElement('img');
			var qrSrc = '${qrAuthnInit}';
			qrCodeImg.src = 'data:image/png;base64,' + qrSrc;
			form.appendChild(qrCodeImg);
			const qrTimeout = setTimeout(function() {
				// submit a qr action on the form after 5 seconds to poll for QR login status
				actionInput.value = 'qr';
				form.submit();
			}, 5000);

			// FIDO Authentication
			var authnButton = document.getElementById("start-authn");
			if (authnButton) {
				authnButton.addEventListener('click', (event) => {
					event.preventDefault();
					// Clear the QR code timeout so it doesn't interrupt the FIDO login process
					window.clearTimeout(qrTimeout);
					var parser = new DOMParser();
					var dom = parser.parseFromString('${fidoAuthnInit}', 'text/html');
					var response = JSON.parse(dom.body.textContent);
					response.challenge = new Uint8Array(b64toBA(b64utob64(response.challenge)));
					if (response.allowCredentials && response.allowCredentials.length > 0) {
						for (const credential of response.allowCredentials) {
							credential.id = new Uint8Array(b64toBA(b64utob64(credential.id)));
						}
					}
					navigator.credentials.get({
						publicKey: response
					}).then((assertion) => {
						const id = assertion.id;
						const rawId = assertion.id;
						const type = assertion.type;
						const clientDataJSONB64u = hextob64u(BAtoHex(new Uint8Array(assertion.response.clientDataJSON)));
						const authenticatorDataCBORB64u = hextob64u(BAtoHex(new Uint8Array(assertion.response.authenticatorData)));
						const signatureB64u = hextob64u(BAtoHex(new Uint8Array(assertion.response.signature)));
						const userHandleB64u = hextob64u(BAtoHex(new Uint8Array(assertion.response.userHandle)));

						// build form fields to send to server in POST
						var form = document.getElementById('kc-login-form');
						if (form) {
							[
								{
									name: 'id',
									value: id
								},
								{
									name: 'rawId',
									value: rawId
								},
								{
									name: 'type',
									value: type
								},
								{
									name: 'client-data-json',
									value: clientDataJSONB64u
								},
								{
									name: 'authenticator-data',
									value: authenticatorDataCBORB64u
								},
								{
									name: 'signature',
									value: signatureB64u
								},
								{
									name: 'user-handle',
									value: userHandleB64u
								},
								{
									name: 'state',
									value: 'authenticate'
								}
							].forEach((field) => {
								const hiddenInput = document.createElement('input');
								hiddenInput.type = 'hidden';
								hiddenInput.name = field.name;
								hiddenInput.value = field.value;
								form.appendChild(hiddenInput);
							});
							document.getElementById('action-type').value = 'fido';
							form.submit();
						}
					})
					.catch((e) => {
						console.error(e);
						console.error(e.message);
					});
				});
			}
        </script>
        <#if client?? && client.baseUrl?has_content>
            <p><a id="backToApplication" href="${client.baseUrl}">${msg("backToApplication")}</a></p>
        </#if>
    </#if>
</@layout.registrationLayout>
