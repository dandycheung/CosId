/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
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

package me.ahoo.cosid.spring.boot.starter.actuate;

import me.ahoo.cosid.provider.IdGeneratorProvider;
import me.ahoo.cosid.spring.boot.starter.ConditionalOnCosIdEnabled;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnCosIdEnabled
@ConditionalOnClass(Endpoint.class)
public class CosIdEndpointAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public CosIdEndpoint cosIdEndpoint(IdGeneratorProvider idGeneratorProvider) {
        return new CosIdEndpoint(idGeneratorProvider);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public CosIdGeneratorEndpoint cosIdGeneratorEndpoint(IdGeneratorProvider idGeneratorProvider) {
        return new CosIdGeneratorEndpoint(idGeneratorProvider);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public CosIdStringGeneratorEndpoint cosIdStringGeneratorEndpoint(IdGeneratorProvider idGeneratorProvider) {
        return new CosIdStringGeneratorEndpoint(idGeneratorProvider);
    }
}
