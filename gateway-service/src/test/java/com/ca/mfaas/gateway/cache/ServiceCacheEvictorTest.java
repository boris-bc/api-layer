package com.ca.mfaas.gateway.cache;/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

import com.ca.mfaas.gateway.discovery.ApimlDiscoveryClient;
import com.ca.mfaas.gateway.ribbon.ApimlZoneAwareLoadBalancer;
import com.ca.mfaas.gateway.security.service.ServiceCacheEvict;
import com.netflix.discovery.CacheRefreshedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class ServiceCacheEvictorTest {

    private ServiceCacheEvictor serviceCacheEvictor;

    private ApimlDiscoveryClient apimlDiscoveryClient = mock(ApimlDiscoveryClient.class);

    private ApimlZoneAwareLoadBalancer apimlZoneAwareLoadBalancer = mock(ApimlZoneAwareLoadBalancer.class);

    private List<ServiceCacheEvict> serviceCacheEvicts = Arrays.asList(
        mock(ServiceCacheEvict.class),
        mock(ServiceCacheEvict.class)
    );

    @Before
    public void setUp() {
        serviceCacheEvictor = new ServiceCacheEvictor(apimlDiscoveryClient, serviceCacheEvicts);
        serviceCacheEvictor.setApimlZoneAwareLoadBalancer(apimlZoneAwareLoadBalancer);
    }

    @Test
    public void testService() {
        serviceCacheEvictor.onEvent(mock(CacheRefreshedEvent.class));
        verify(apimlZoneAwareLoadBalancer, never()).serverChanged();

        serviceCacheEvictor.evictCacheService("service1");
        serviceCacheEvictor.evictCacheService("service1");
        serviceCacheEvictor.evictCacheService("service2");
        serviceCacheEvictor.onEvent(mock(CacheRefreshedEvent.class));
        serviceCacheEvicts.forEach(x -> {
            verify(x, times(1)).evictCacheService("service1");
            verify(x, times(1)).evictCacheService("service2");
        });
        verify(apimlZoneAwareLoadBalancer, times(1)).serverChanged();

        serviceCacheEvictor.evictCacheService("service3");
        serviceCacheEvictor.evictCacheAllService();
        serviceCacheEvictor.evictCacheService("service4");
        serviceCacheEvictor.onEvent(mock(CacheRefreshedEvent.class));
        serviceCacheEvicts.forEach(x -> {
            verify(x, never()).evictCacheService("service3");
            verify(x, times(1)).evictCacheAllService();
        });
        verify(apimlZoneAwareLoadBalancer, times(2)).serverChanged();
    }

}
