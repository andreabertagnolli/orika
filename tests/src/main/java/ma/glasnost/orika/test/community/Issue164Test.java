package ma.glasnost.orika.test.community;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.NullFilter;
import ma.glasnost.orika.OrikaSystemProperties;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.junit.Assert;
import org.junit.Test;

public class Issue164Test {

    @Test
    public void mapping_context_does_not_change_resolved_types_on_the_same_level() {
        System.setProperty(OrikaSystemProperties.WRITE_SOURCE_FILES, "true");
        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.classMap(SourceObjectA.class, ObjectA.class)
                .byDefault()
                .toClassMap();
        factory.classMap(SourceSubObjectA.class, SubObjectA.class)
                .byDefault()
                .toClassMap();
        factory.classMap(SourceSubObjectB.class, SubObjectB.class)
                .byDefault()
                .toClassMap();
        factory.classMap(SourceSubSubObjectA.class, SubSubObjectA.class)
                .byDefault()
                .toClassMap();
        factory.registerFilter(new TestFilter());

        SourceObjectA sourceObj = new SourceObjectA();
        sourceObj.setFoo1("foo1");
        sourceObj.setFoo2("foo2");
        sourceObj.setFoo3("foo3");

        SourceSubObjectA sourceSubObj = new SourceSubObjectA();
        sourceSubObj.setBar1("bar1");
        sourceSubObj.setBar2("bar2");
        sourceSubObj.setBar3("bar3");
        sourceObj.setObjA(sourceSubObj);

        SourceSubObjectB sourceSubObjB = new SourceSubObjectB();
        sourceSubObjB.setFooBar1("fooBar1");
        sourceSubObjB.setFooBar2("fooBar2");

        SourceSubSubObjectA sourceSubSubObj = new SourceSubSubObjectA();
        sourceSubSubObj.setStr1("str1");
        sourceSubSubObj.setStr2("str2");
        sourceSubSubObj.setStr4("str4");
        sourceSubSubObj.setStr7("str7");

        sourceSubObjB.setSubFooBar(sourceSubSubObj);
        sourceObj.setObjB(sourceSubObjB);

        ObjectA destObj = factory.getMapperFacade().map(sourceObj, ObjectA.class);

        Assert.assertEquals(sourceObj.getFoo1(), destObj.getFoo1());
        Assert.assertEquals(sourceObj.getObjA().getBar1(), destObj.getObjA().getBar1());
        Assert.assertNotNull(destObj.getObjB());

        if (destObj.getObjB() != null) {
            Assert.assertEquals(sourceObj.getObjB().getFooBar1(), destObj.getObjB().getFooBar1());
            Assert.assertEquals(sourceObj.getObjB().getSubFooBar().getStr7(), destObj.getObjB().getSubFooBar().getStr7());
        }
    }

    public static class TestFilter extends NullFilter<Object, Object> {
        @Override
        public <S, D> boolean shouldMap(final Type<S> sourceType, final String sourceName, final S source, final Type<D> destinationType,
                                        final String destinationName, final D destination, final MappingContext mappingContext) {
            Class<?> destKlass = mappingContext.getResolvedDestinationType().getRawType();
            Class<?> sourceKlass = mappingContext.getResolvedSourceType().getRawType();

            try {
                destKlass.getDeclaredField(destinationName);
            } catch (NoSuchFieldException nsfe) {
                System.out.println("destination class: " + destKlass.getName());
                System.out.println("source class: " + sourceKlass.getName());
                nsfe.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public static class ObjectA {

        private SubObjectA objA;
        private SubObjectB objB;
        private String foo1;
        private String foo2;
        private String foo3;

        public SubObjectA getObjA() {
            return objA;
        }
        public void setObjA(SubObjectA objA) {
            this.objA = objA;
        }
        public SubObjectB getObjB() {
            return objB;
        }
        public void setObjB(SubObjectB objB) {
            this.objB = objB;
        }
        public String getFoo1() {
            return foo1;
        }
        public void setFoo1(String foo1) {
            this.foo1 = foo1;
        }
        public String getFoo2() {
            return foo2;
        }
        public void setFoo2(String foo2) {
            this.foo2 = foo2;
        }
        public String getFoo3() {
            return foo3;
        }
        public void setFoo3(String foo3) {
            this.foo3 = foo3;
        }
    }

    public static class SourceObjectA {
        private SourceSubObjectA objA;
        private SourceSubObjectB objB;
        private String foo1;
        private String foo2;
        private String foo3;

        public SourceSubObjectA getObjA() {
            return objA;
        }
        public void setObjA(SourceSubObjectA objA) {
            this.objA = objA;
        }
        public SourceSubObjectB getObjB() {
            return objB;
        }
        public void setObjB(SourceSubObjectB objB) {
            this.objB = objB;
        }
        public String getFoo1() {
            return foo1;
        }
        public void setFoo1(String foo1) {
            this.foo1 = foo1;
        }
        public String getFoo2() {
            return foo2;
        }
        public void setFoo2(String foo2) {
            this.foo2 = foo2;
        }
        public String getFoo3() {
            return foo3;
        }
        public void setFoo3(String foo3) {
            this.foo3 = foo3;
        }
    }

    public static class SourceSubObjectA {

        private String bar1;
        private String bar2;
        private String bar3;

        public String getBar1() {
            return bar1;
        }
        public void setBar1(String bar1) {
            this.bar1 = bar1;
        }
        public String getBar2() {
            return bar2;
        }
        public void setBar2(String bar2) {
            this.bar2 = bar2;
        }
        public String getBar3() {
            return bar3;
        }
        public void setBar3(String bar3) {
            this.bar3 = bar3;
        }
    }

    public static class SourceSubObjectB {

        private String fooBar1;
        private String fooBar2;
        private SourceSubSubObjectA subFooBar;

        public String getFooBar1() {
            return fooBar1;
        }
        public void setFooBar1(String fooBar1) {
            this.fooBar1 = fooBar1;
        }
        public String getFooBar2() {
            return fooBar2;
        }
        public void setFooBar2(String fooBar2) {
            this.fooBar2 = fooBar2;
        }
        public SourceSubSubObjectA getSubFooBar() {
            return subFooBar;
        }
        public void setSubFooBar(SourceSubSubObjectA subFooBar) {
            this.subFooBar = subFooBar;
        }
    }

    public static class SourceSubSubObjectA {

        private String str1;
        private String str2;
        private String str3;
        private String str4;
        private String str5;
        private String str6;
        private String str7;

        public String getStr1() {
            return str1;
        }
        public void setStr1(String str1) {
            this.str1 = str1;
        }
        public String getStr2() {
            return str2;
        }
        public void setStr2(String str2) {
            this.str2 = str2;
        }
        public String getStr3() {
            return str3;
        }
        public void setStr3(String str3) {
            this.str3 = str3;
        }
        public String getStr4() {
            return str4;
        }
        public void setStr4(String str4) {
            this.str4 = str4;
        }
        public String getStr5() {
            return str5;
        }
        public void setStr5(String str5) {
            this.str5 = str5;
        }
        public String getStr6() {
            return str6;
        }
        public void setStr6(String str6) {
            this.str6 = str6;
        }
        public String getStr7() {
            return str7;
        }
        public void setStr7(String str7) {
            this.str7 = str7;
        }
    }

    public static class SubObjectA {

        private String bar1;
        private String bar2;
        private String bar3;

        public String getBar1() {
            return bar1;
        }
        public void setBar1(String bar1) {
            this.bar1 = bar1;
        }
        public String getBar2() {
            return bar2;
        }
        public void setBar2(String bar2) {
            this.bar2 = bar2;
        }
        public String getBar3() {
            return bar3;
        }
        public void setBar3(String bar3) {
            this.bar3 = bar3;
        }
    }

    public static class SubObjectB {

        private String fooBar1;
        private String fooBar2;
        private SubSubObjectA subFooBar;

        public String getFooBar1() {
            return fooBar1;
        }
        public void setFooBar1(String fooBar1) {
            this.fooBar1 = fooBar1;
        }
        public String getFooBar2() {
            return fooBar2;
        }
        public void setFooBar2(String fooBar2) {
            this.fooBar2 = fooBar2;
        }
        public SubSubObjectA getSubFooBar() {
            return subFooBar;
        }
        public void setSubFooBar(SubSubObjectA subFooBar) {
            this.subFooBar = subFooBar;
        }
    }

    public static class SubSubObjectA {

        private String str1;
        private String str2;
        private String str3;
        private String str4;
        private String str5;
        private String str6;
        private String str7;

        public String getStr1() {
            return str1;
        }
        public void setStr1(String str1) {
            this.str1 = str1;
        }
        public String getStr2() {
            return str2;
        }
        public void setStr2(String str2) {
            this.str2 = str2;
        }
        public String getStr3() {
            return str3;
        }
        public void setStr3(String str3) {
            this.str3 = str3;
        }
        public String getStr4() {
            return str4;
        }
        public void setStr4(String str4) {
            this.str4 = str4;
        }
        public String getStr5() {
            return str5;
        }
        public void setStr5(String str5) {
            this.str5 = str5;
        }
        public String getStr6() {
            return str6;
        }
        public void setStr6(String str6) {
            this.str6 = str6;
        }
        public String getStr7() {
            return str7;
        }
        public void setStr7(String str7) {
            this.str7 = str7;
        }
    }

}
