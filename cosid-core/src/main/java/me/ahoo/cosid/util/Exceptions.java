/*
 * Copyright [2021-2021] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.cosid.util;

import me.ahoo.cosid.CosIdException;

/**
 * @author ahoo wang
 */
public final class Exceptions {

    public static <TResult> TResult invokeUnchecked(CheckedFunction<TResult> checkedFunction) {
        try {
            return checkedFunction.invoke();
        } catch (RuntimeException | Error runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new CosIdException(exception.getMessage(),exception);
        }
    }

    @FunctionalInterface
    public interface CheckedFunction<TResult> {
        TResult invoke() throws Exception;
    }
}