/*
 * Copyright 2017 Alex Thomson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mojang.authlib.properties;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import org.apache.commons.codec.binary.Base64;

public class Property {
	
	private final String name;
	private final String value;
	private final String signature;
	
	public Property(String value, String name) {
		this(value, name, null);
	}
	
	public Property(String name, String value, String signature) {
		this.name = name;
		this.value = value;
		this.signature = signature;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getSignature() {
		return this.signature;
	}
	
	public Boolean hasSignature() {
		return this.signature != null;
	}
	
	public boolean isSignatureValid(PublicKey publicKey) {
		try {
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(publicKey);
			signature.update(this.value.getBytes());
			return signature.verify(Base64.decodeBase64(this.signature));
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
