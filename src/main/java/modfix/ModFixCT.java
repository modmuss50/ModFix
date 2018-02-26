package modfix;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;


public class ModFixCT implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        //Fix taken from https://github.com/Xalcon/TorchMaster/pull/50/files
        if (name.equals("net.xalcon.torchmaster.common.TorchRegistry")) {
            ClassNode classNode = readClassFromBytes(basicClass);
            classNode.methods.stream()
                    .filter(methodNode -> methodNode.name.equals("registerTorch"))
                    .forEach(methodNode -> {
                        System.out.println("Found registerTorch method");
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        String desc = "(Lnet/minecraft/world/World;)Z";
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "modfix/ModFixCT", "isBroken", desc, false));
                        LabelNode l1 = new LabelNode();
                        insnList.add(new JumpInsnNode(Opcodes.IFEQ, l1));
                        insnList.add(new InsnNode(Opcodes.RETURN));
                        insnList.add(l1);
                        insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insnList);
                        System.out.println("Patched registerTorch");
                    });

            try {
                //TODO dont ship this
                FileUtils.writeByteArrayToFile(new File("TorchRegistry.class"), writeClassToBytes(classNode));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return writeClassToBytes(classNode);
        }
        return basicClass;
    }

    public static boolean isBroken(World world){
        if(world == null || world.provider == null){
            System.out.println("Torch master tried to load a torch in a null world. Mod Fix caught this error");
            return true;
        }
        return false;
    }

    private ClassNode readClassFromBytes(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        return classNode;
    }

    private byte[] writeClassToBytes(ClassNode classNode) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
