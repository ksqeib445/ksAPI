/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.ksqeib.ksapi.mysql.serializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * 序列化接口
 *
 * @param <T> 序列化的东西的泛型
 */
public interface Serializer<T> extends JsonSerializer<T>, JsonDeserializer<T> {

}