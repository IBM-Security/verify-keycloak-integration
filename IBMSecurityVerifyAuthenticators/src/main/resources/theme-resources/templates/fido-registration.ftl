<#import "template.ftl" as layout>
<@layout.registrationLayout showAnotherWayIfPresent=false; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        ${msg("loginTitleHtml",realm.name)}
    <#elseif section = "form">
        <div align="center">
            <form id="kc-fido-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			    <input type="hidden" name="action" id="action-input"/>
			    <input type="hidden" name="type" id="type-input" />
			    <input type="hidden" name="id" id="id-input" />
			    <input type="hidden" name="rawId" id="raw-id-input" />
			    <input type="hidden" name="clientDataJSON" id="client-data-json-input" />
			    <input type="hidden" name="attestationObject" id="attestation-object-input" />
                <label for="fidoDevicenickname" class="control-label" align="left">FIDO2 Device Nickname</label>
                <input type="text" name="fidoDeviceNickname" id="fidoDevicenickname" class="form-control">
            </form>
            <br><br>
		    <button id="registration-required" class="btn btn-primary btn-block btn-lg">
                ${msg("fidoRegisterButton")}
            </button>
		</div>
        <script type="text/javascript">
			// Various Base 64 utilities
			var Base64Binary = {
				_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

				decodeArrayBuffer: function(input) {
					var bytes = (input.length/4) * 3;
					var ab = new ArrayBuffer(bytes);
					this.decode(input, ab);

					return ab;
				},

				removePaddingChars: function(input){
					var lkey = this._keyStr.indexOf(input.charAt(input.length - 1));
					if(lkey == 64){
						return input.substring(0,input.length - 1);
					}
					return input;
				},

				decode: function (input, isBase64Url, arrayBuffer) {
					if(isBase64Url) {
						const deurled = input.replace(/-/g, '+').replace(/_/g, '\/');
						return Base64Binary.decode(deurled, false);
					}

					//get last chars to see if are valid
					input = this.removePaddingChars(input);
					input = this.removePaddingChars(input);

					var bytes = parseInt((input.length / 4) * 3, 10);

					var uarray;
					var chr1, chr2, chr3;
					var enc1, enc2, enc3, enc4;
					var i = 0;
					var j = 0;

					if (arrayBuffer) {
						uarray = new Uint8Array(arrayBuffer);
					} else {
						uarray = new Uint8Array(bytes);
					}

					input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

					for (i=0; i<bytes; i+=3) {
						//get the 3 octects in 4 ascii chars
						enc1 = this._keyStr.indexOf(input.charAt(j++));
						enc2 = this._keyStr.indexOf(input.charAt(j++));
						enc3 = this._keyStr.indexOf(input.charAt(j++));
						enc4 = this._keyStr.indexOf(input.charAt(j++));

						chr1 = (enc1 << 2) | (enc2 >> 4);
						chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
						chr3 = ((enc3 & 3) << 6) | enc4;

						uarray[i] = chr1;
						if (enc3 != 64) uarray[i+1] = chr2;
						if (enc4 != 64) uarray[i+2] = chr3;
					}

					return uarray;
				}
			};

			var base64url = {
				encode: function (input, encoding) {
					encoding = encoding || "utf8";
					return this.fromBase64(input.toString("base64"));
				},
				fromBase64: function(base64) {
					return base64
						.replace(/=/g, "")
						.replace(/\+/g, "-")
						.replace(/\//g, "_");
				}
			};

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
				if (b64pad) { while ((ret.length & three) > zero) { ret += b64pad; } }
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
			// End of base 64 utilities

			// FIDO Registration
			// Get the form and action input elements to control the form submission flow
			var form = document.getElementById('kc-fido-login-form');
			var actionInput = document.getElementById('action-input');

			// Button to initiate FIDO registration
			var regButton = document.getElementById("registration-required");
			if (regButton) {
				regButton.addEventListener('click', (event) => {
					event.preventDefault();
					actionInput.value = 'register';
					var parser = new DOMParser();
					var dom = parser.parseFromString('${fidoRegInit}', 'text/html');
					var response = JSON.parse(dom.body.textContent);
					response.challenge = Base64Binary.decode(response.challenge, true);
					response.user.id = Base64Binary.decode(response.user.id, true);
					response.excludeCredentials.forEach((cred) => {
						cred.id = Base64Binary.decode(cred.id, true);
					});
					navigator.credentials.create({
						publicKey: response
					})
					.then(
						(cred) => {
							document.getElementById('type-input').value = cred.type;
							document.getElementById('id-input').value = cred.id;
							document.getElementById('raw-id-input').value = cred.id;
							document.getElementById('client-data-json-input').value = hextob64u(BAtoHex(new Uint8Array(cred.response.clientDataJSON)));
							document.getElementById('attestation-object-input').value = hextob64u(BAtoHex(new Uint8Array(cred.response.attestationObject)));
							actionInput.value = 'register';
							form.submit();
						}
					)
					.catch((e) => {
						console.error(e);
						console.error(e.message);
					});
				});
			}
        </script>
    </#if>
</@layout.registrationLayout>
