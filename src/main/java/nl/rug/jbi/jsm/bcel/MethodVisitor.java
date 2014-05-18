package nl.rug.jbi.jsm.bcel;

import nl.rug.jbi.jsm.core.event.EventBus;
import org.apache.bcel.generic.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class MethodVisitor extends EmptyVisitor {
    private final static Logger logger = LogManager.getLogger(MethodVisitor.class);

    private final MethodGen mg;
    private final EventBus eBus;
    private final ConstantPoolGen cp;

    public MethodVisitor(final MethodGen mg, EventBus eBus) {
        this.mg = mg;
        this.eBus = eBus;
        this.cp = mg.getConstantPool();
    }

    public void start() {
        if (!(mg.isAbstract() || mg.isNative())) {
            InstructionHandle ih = mg.getInstructionList().getStart();
            while (ih != null) {
                final Instruction i = ih.getInstruction();

                i.accept(this);

                ih = ih.getNext();
            }

            handleLocalVariables();
            handleExceptionHandlers();
        }
    }

    private void handleLocalVariables() {
        final LocalVariableGen[] lVarGens = mg.getLocalVariables();
        logger.trace(Arrays.asList(lVarGens));

        if (!this.eBus.hasListeners(LocalVariableDefinition.class)) return;

        for (final LocalVariableGen lVarGen : lVarGens) {
            this.eBus.publish(new LocalVariableDefinition(lVarGen));
        }
    }

    private void handleExceptionHandlers() {
        final CodeExceptionGen[] cegs = mg.getExceptionHandlers();
        logger.trace(Arrays.asList(cegs));

        if (!this.eBus.hasListeners(ExceptionHandlerDefinition.class)) return;

        for (final CodeExceptionGen ceg : cegs) {
            this.eBus.publish(new ExceptionHandlerDefinition(ceg));
        }
    }

    @Override
    public void visitStackInstruction(StackInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLocalVariableInstruction(LocalVariableInstruction obj) {
        logger.trace(obj);

        /* Used in CKJM, replaced by LocalVariable iteration */
    }

    @Override
    public void visitBranchInstruction(BranchInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLoadClass(LoadClass obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFieldInstruction(FieldInstruction obj) {
        logger.trace(obj);

        /** Field access. */
        if (this.eBus.hasListeners(FieldAccessInstr.class)) {
            this.eBus.publish(new FieldAccessInstr(obj, this.cp));
        }
    }

    @Override
    public void visitIfInstruction(IfInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConversionInstruction(ConversionInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitPopInstruction(PopInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitStoreInstruction(StoreInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitTypedInstruction(TypedInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitSelect(Select obj) {
        logger.trace(obj);
    }

    @Override
    public void visitJsrInstruction(JsrInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitGotoInstruction(GotoInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitUnconditionalBranch(UnconditionalBranch obj) {
        logger.trace(obj);
    }

    @Override
    public void visitPushInstruction(PushInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitArithmeticInstruction(ArithmeticInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitCPInstruction(CPInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitInvokeInstruction(InvokeInstruction obj) {
        logger.trace(obj);

        /** Method invocation. */
        if (this.eBus.hasListeners(InvokeMethodInstr.class)) {
            this.eBus.publish(new InvokeMethodInstr(obj, this.cp));
        }
    }

    @Override
    public void visitArrayInstruction(ArrayInstruction obj) {
        logger.trace(obj);

        /* Used in CKJM, replaced by LocalVariable iteration */
    }

    @Override
    public void visitAllocationInstruction(AllocationInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitReturnInstruction(ReturnInstruction obj) {
        logger.trace(obj);

        /** Visit return instruction. */
        if (this.eBus.hasListeners(TypeUseInstruction.class)) {
            this.eBus.publish(new TypeUseInstruction(obj.getType(this.cp)));
        }
    }

    @Override
    public void visitFieldOrMethod(FieldOrMethod obj) {
        logger.trace(obj);
    }

    @Override
    public void visitConstantPushInstruction(ConstantPushInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitExceptionThrower(ExceptionThrower obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLoadInstruction(LoadInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitVariableLengthInstruction(VariableLengthInstruction obj) {
        logger.trace(obj);
    }

    @Override
    public void visitStackProducer(StackProducer obj) {
        logger.trace(obj);
    }

    @Override
    public void visitStackConsumer(StackConsumer obj) {
        logger.trace(obj);
    }

    @Override
    public void visitACONST_NULL(ACONST_NULL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitGETSTATIC(GETSTATIC obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ICMPLT(IF_ICMPLT obj) {
        logger.trace(obj);
    }

    @Override
    public void visitMONITOREXIT(MONITOREXIT obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIFLT(IFLT obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLSTORE(LSTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitPOP2(POP2 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitBASTORE(BASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitISTORE(ISTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitCHECKCAST(CHECKCAST obj) {
        logger.trace(obj);

        /** Visit checkcast instruction. */
        if (this.eBus.hasListeners(TypeUseInstruction.class)) {
            this.eBus.publish(new TypeUseInstruction(obj.getType(this.cp)));
        }
    }

    @Override
    public void visitFCMPG(FCMPG obj) {
        logger.trace(obj);
    }

    @Override
    public void visitI2F(I2F obj) {
        logger.trace(obj);
    }

    @Override
    public void visitATHROW(ATHROW obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDCMPL(DCMPL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitARRAYLENGTH(ARRAYLENGTH obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDUP(DUP obj) {
        logger.trace(obj);
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLCONST(LCONST obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDREM(DREM obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIFGE(IFGE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitCALOAD(CALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLASTORE(LASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitI2D(I2D obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDADD(DADD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIAND(IAND obj) {
        logger.trace(obj);
    }

    @Override
    public void visitPUTFIELD(PUTFIELD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitILOAD(ILOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDLOAD(DLOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDCONST(DCONST obj) {
        logger.trace(obj);
    }

    @Override
    public void visitNEW(NEW obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIFNULL(IFNULL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLSUB(LSUB obj) {
        logger.trace(obj);
    }

    @Override
    public void visitL2I(L2I obj) {
        logger.trace(obj);
    }

    @Override
    public void visitISHR(ISHR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitTABLESWITCH(TABLESWITCH obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIINC(IINC obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDRETURN(DRETURN obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFSTORE(FSTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDASTORE(DASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIALOAD(IALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDDIV(DDIV obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ICMPGE(IF_ICMPGE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLAND(LAND obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIDIV(IDIV obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLOR(LOR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitCASTORE(CASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFREM(FREM obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLDC(LDC obj) {
        logger.trace(obj);
    }

    @Override
    public void visitBIPUSH(BIPUSH obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDSTORE(DSTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitF2L(F2L obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFMUL(FMUL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLLOAD(LLOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitJSR(JSR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFSUB(FSUB obj) {
        logger.trace(obj);
    }

    @Override
    public void visitSASTORE(SASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitALOAD(ALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDUP2_X2(DUP2_X2 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitRETURN(RETURN obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDALOAD(DALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitSIPUSH(SIPUSH obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDSUB(DSUB obj) {
        logger.trace(obj);
    }

    @Override
    public void visitL2F(L2F obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ICMPGT(IF_ICMPGT obj) {
        logger.trace(obj);
    }

    @Override
    public void visitF2D(F2D obj) {
        logger.trace(obj);
    }

    @Override
    public void visitI2L(I2L obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ACMPNE(IF_ACMPNE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitPOP(POP obj) {
        logger.trace(obj);
    }

    @Override
    public void visitI2S(I2S obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIFEQ(IFEQ obj) {
        logger.trace(obj);
    }

    @Override
    public void visitSWAP(SWAP obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIOR(IOR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIREM(IREM obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIASTORE(IASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitNEWARRAY(NEWARRAY obj) {
        logger.trace(obj);
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitINEG(INEG obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLCMP(LCMP obj) {
        logger.trace(obj);
    }

    @Override
    public void visitJSR_W(JSR_W obj) {
        logger.trace(obj);
    }

    @Override
    public void visitMULTIANEWARRAY(MULTIANEWARRAY obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDUP_X2(DUP_X2 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitSALOAD(SALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIFNONNULL(IFNONNULL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDMUL(DMUL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIFNE(IFNE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ICMPLE(IF_ICMPLE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLDC2_W(LDC2_W obj) {
        logger.trace(obj);
    }

    @Override
    public void visitGETFIELD(GETFIELD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLADD(LADD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitNOP(NOP obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFALOAD(FALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitINSTANCEOF(INSTANCEOF obj) {
        logger.trace(obj);

        /** Visit an instanceof instruction. */
        if (this.eBus.hasListeners(TypeUseInstruction.class)) {
            this.eBus.publish(new TypeUseInstruction(obj.getType(this.cp)));
        }
    }

    @Override
    public void visitIFLE(IFLE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLXOR(LXOR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLRETURN(LRETURN obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFCONST(FCONST obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIUSHR(IUSHR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitBALOAD(BALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDUP2(DUP2 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ACMPEQ(IF_ACMPEQ obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIMPDEP1(IMPDEP1 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitMONITORENTER(MONITORENTER obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLSHL(LSHL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDCMPG(DCMPG obj) {
        logger.trace(obj);
    }

    @Override
    public void visitD2L(D2L obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIMPDEP2(IMPDEP2 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitL2D(L2D obj) {
        logger.trace(obj);
    }

    @Override
    public void visitRET(RET obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIFGT(IFGT obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIXOR(IXOR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFASTORE(FASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIRETURN(IRETURN obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ICMPNE(IF_ICMPNE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFLOAD(FLOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLDIV(LDIV obj) {
        logger.trace(obj);
    }

    @Override
    public void visitPUTSTATIC(PUTSTATIC obj) {
        logger.trace(obj);
    }

    @Override
    public void visitAALOAD(AALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitD2I(D2I obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIF_ICMPEQ(IF_ICMPEQ obj) {
        logger.trace(obj);
    }

    @Override
    public void visitAASTORE(AASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitARETURN(ARETURN obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDUP2_X1(DUP2_X1 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFNEG(FNEG obj) {
        logger.trace(obj);
    }

    @Override
    public void visitGOTO_W(GOTO_W obj) {
        logger.trace(obj);
    }

    @Override
    public void visitD2F(D2F obj) {
        logger.trace(obj);
    }

    @Override
    public void visitGOTO(GOTO obj) {
        logger.trace(obj);
    }

    @Override
    public void visitISUB(ISUB obj) {
        logger.trace(obj);
    }

    @Override
    public void visitF2I(F2I obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDNEG(DNEG obj) {
        logger.trace(obj);
    }

    @Override
    public void visitICONST(ICONST obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFDIV(FDIV obj) {
        logger.trace(obj);
    }

    @Override
    public void visitI2B(I2B obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLNEG(LNEG obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLREM(LREM obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIMUL(IMUL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitIADD(IADD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLSHR(LSHR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLOOKUPSWITCH(LOOKUPSWITCH obj) {
        logger.trace(obj);
    }

    @Override
    public void visitDUP_X1(DUP_X1 obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFCMPL(FCMPL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitI2C(I2C obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLMUL(LMUL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLUSHR(LUSHR obj) {
        logger.trace(obj);
    }

    @Override
    public void visitISHL(ISHL obj) {
        logger.trace(obj);
    }

    @Override
    public void visitLALOAD(LALOAD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitASTORE(ASTORE obj) {
        logger.trace(obj);
    }

    @Override
    public void visitANEWARRAY(ANEWARRAY obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFRETURN(FRETURN obj) {
        logger.trace(obj);
    }

    @Override
    public void visitFADD(FADD obj) {
        logger.trace(obj);
    }

    @Override
    public void visitBREAKPOINT(BREAKPOINT obj) {
        logger.trace(obj);
    }
}
