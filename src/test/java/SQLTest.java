/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.camel.util.KeyValueHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;





public class SQLTest extends CamelBlueprintTestSupport {
   
    private EmbeddedDatabase db;

    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.DERBY).addScript("sql/createAndPopulateDatabase.sql").build();
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        db.shutdown();
    }
    
    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void testReadRegion() throws Exception {
    	
    	 context.getRouteDefinition("_routePrincipal").adviceWith(context, 
 				new AdviceWithRouteBuilder() {
 		
 			@Override
 			public void configure() throws Exception {
 				weaveById("_toQueryRegions").before()
 		    	.log("[LOG TEST] ANTES DE LEER LOS REGISTROS");
 				
 				weaveById("_toQueryRegions").after()
 		    	.log("[LOG TEST] DESPUES DE LEER LOS REGISTROS");
 				
 				weaveById("_logRegion").after()
 		    	.to("mock:testValidate");
 			}
 		});
    
    	MockEndpoint mockTestValidate = MockEndpoint.resolve(context, "mock:testValidate");
    	List<String> expectedBodiesValidate = new ArrayList<String>();
    	expectedBodiesValidate.add("[Region = regionId:1, regionName:Europe]");
    	expectedBodiesValidate.add("[Region = regionId:2, regionName:Americas]");
    	expectedBodiesValidate.add("[Region = regionId:3, regionName:Asia]");
    	mockTestValidate.expectedBodiesReceived(expectedBodiesValidate);
    	
    	mockTestValidate.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	//El componente SQL ahora apunta a la BD en memoria! No al H2!
                getContext().getComponent("sql", SqlComponent.class).setDataSource(db);
            }
        };
    }
    
}