/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.github.mxd888.socket.utils.cache;

import java.util.Objects;

public enum CacheChangeType {

	/**
	 * key级别清空本地缓存
	 */
	REMOVE(1),

	/**
	 * key级别清空本地缓存
	 */
	UPDATE(2),

	/**
	 * key级别清空本地缓存
	 */
	PUT(3),

	/**
	 * cacheName级别清空本地缓存
	 */
	CLEAR(4);

	public static CacheChangeType from(Integer method) {
		CacheChangeType[] values = CacheChangeType.values();
		for (CacheChangeType v : values) {
			if (Objects.equals(v.value, method)) {
				return v;
			}
		}
		return null;
	}

	Integer value;

	CacheChangeType(Integer value) {
		this.value = value;
	}
}
