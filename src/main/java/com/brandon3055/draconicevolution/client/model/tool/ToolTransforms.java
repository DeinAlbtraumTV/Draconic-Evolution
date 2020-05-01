//package com.brandon3055.draconicevolution.client.model.tool;
//
// import com.google.common.collect.ImmutableMap;
//import net.minecraft.client.renderer.model.ItemCameraTransforms;
//
//
//import static codechicken.lib.util.TransformUtils.create;
//import static codechicken.lib.util.TransformUtils.flipLeft;
//
///**
// * Created by brandon3055 on 1/09/2016.
// */
//public class ToolTransforms {
//
//    public static final CCModelState STAFF_STATE;
//    public static final CCModelState DR_SWORD_STATE;
//    public static final CCModelState WY_SWORD_STATE;
//
//    static {
//        TRSRTransformation thirdPerson = create(0, 3, 1, 0, 0, 0, 0.55f);
//        TRSRTransformation firstPerson = create(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
//        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> defaultItemBuilder = ImmutableMap.builder();
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.GROUND, create(1, 4, 0, 0, 0, 0, 0.3f));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIXED, create(2, 2, 0, 0, 0, 0, 0.55f));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.HEAD, create(0, 13, 7, 0, 180, 0, 1));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, flipLeft(thirdPerson));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, flipLeft(firstPerson));
//        STAFF_STATE = new CCModelState(defaultItemBuilder.build());
//
//        thirdPerson = create(0, 3, 1, 0, 0, 0, 0.55f);
//        firstPerson = create(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
//        defaultItemBuilder = ImmutableMap.builder();
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.GROUND, create(-2, 0, 0, 0, 0, 0, 0.4f));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIXED, create(-3, -3, 0, 0, 0, 0, 0.65f));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.HEAD, create(0, 13, 7, 0, 180, 0, 1));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, flipLeft(thirdPerson));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, flipLeft(firstPerson));
//        DR_SWORD_STATE = new CCModelState(defaultItemBuilder.build());
//
//        thirdPerson = create(0, 3, 1, 0, 0, 0, 0.55f);
//        firstPerson = create(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
//        defaultItemBuilder = ImmutableMap.builder();
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.GROUND, create(-2, 1, 0, 0, 0, 0, 0.4f));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIXED, create(-2, -2, 0, 0, 0, 0, 0.8f));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.HEAD, create(0, 13, 7, 0, 180, 0, 1));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, flipLeft(thirdPerson));
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
//        defaultItemBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, flipLeft(firstPerson));
//        WY_SWORD_STATE = new CCModelState(defaultItemBuilder.build());
//    }
//
//}
