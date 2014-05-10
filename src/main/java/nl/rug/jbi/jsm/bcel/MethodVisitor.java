package nl.rug.jbi.jsm.bcel;

import nl.rug.jbi.jsm.core.EventBus;
import org.apache.bcel.generic.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MethodVisitor extends EmptyVisitor {
    private final static Logger logger = LogManager.getLogger(MethodVisitor.class);

    private final MethodGen mg;
    private final ConstantPoolGen cp;

    public MethodVisitor(final MethodGen mg, EventBus eBus) {
        this.mg = mg;
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
        }
    }

    @Override
    public void visitStackInstruction(StackInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLocalVariableInstruction(LocalVariableInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitBranchInstruction(BranchInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLoadClass(LoadClass obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFieldInstruction(FieldInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIfInstruction(IfInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConversionInstruction(ConversionInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitPopInstruction(PopInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitStoreInstruction(StoreInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitTypedInstruction(TypedInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitSelect(Select obj) {
        logger.debug(obj);
    }

    @Override
    public void visitJsrInstruction(JsrInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitGotoInstruction(GotoInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitUnconditionalBranch(UnconditionalBranch obj) {
        logger.debug(obj);
    }

    @Override
    public void visitPushInstruction(PushInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitArithmeticInstruction(ArithmeticInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitCPInstruction(CPInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitInvokeInstruction(InvokeInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitArrayInstruction(ArrayInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitAllocationInstruction(AllocationInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitReturnInstruction(ReturnInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFieldOrMethod(FieldOrMethod obj) {
        logger.debug(obj);
    }

    @Override
    public void visitConstantPushInstruction(ConstantPushInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitExceptionThrower(ExceptionThrower obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLoadInstruction(LoadInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitVariableLengthInstruction(VariableLengthInstruction obj) {
        logger.debug(obj);
    }

    @Override
    public void visitStackProducer(StackProducer obj) {
        logger.debug(obj);
    }

    @Override
    public void visitStackConsumer(StackConsumer obj) {
        logger.debug(obj);
    }

    @Override
    public void visitACONST_NULL(ACONST_NULL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitGETSTATIC(GETSTATIC obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ICMPLT(IF_ICMPLT obj) {
        logger.debug(obj);
    }

    @Override
    public void visitMONITOREXIT(MONITOREXIT obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFLT(IFLT obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLSTORE(LSTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitPOP2(POP2 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitBASTORE(BASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitISTORE(ISTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitCHECKCAST(CHECKCAST obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFCMPG(FCMPG obj) {
        logger.debug(obj);
    }

    @Override
    public void visitI2F(I2F obj) {
        logger.debug(obj);
    }

    @Override
    public void visitATHROW(ATHROW obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDCMPL(DCMPL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitARRAYLENGTH(ARRAYLENGTH obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDUP(DUP obj) {
        logger.debug(obj);
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLCONST(LCONST obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDREM(DREM obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFGE(IFGE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitCALOAD(CALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLASTORE(LASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitI2D(I2D obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDADD(DADD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIAND(IAND obj) {
        logger.debug(obj);
    }

    @Override
    public void visitPUTFIELD(PUTFIELD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitILOAD(ILOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDLOAD(DLOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDCONST(DCONST obj) {
        logger.debug(obj);
    }

    @Override
    public void visitNEW(NEW obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFNULL(IFNULL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLSUB(LSUB obj) {
        logger.debug(obj);
    }

    @Override
    public void visitL2I(L2I obj) {
        logger.debug(obj);
    }

    @Override
    public void visitISHR(ISHR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitTABLESWITCH(TABLESWITCH obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIINC(IINC obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDRETURN(DRETURN obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFSTORE(FSTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDASTORE(DASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIALOAD(IALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDDIV(DDIV obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ICMPGE(IF_ICMPGE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLAND(LAND obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIDIV(IDIV obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLOR(LOR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitCASTORE(CASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFREM(FREM obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLDC(LDC obj) {
        logger.debug(obj);
    }

    @Override
    public void visitBIPUSH(BIPUSH obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDSTORE(DSTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitF2L(F2L obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFMUL(FMUL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLLOAD(LLOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitJSR(JSR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFSUB(FSUB obj) {
        logger.debug(obj);
    }

    @Override
    public void visitSASTORE(SASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitALOAD(ALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDUP2_X2(DUP2_X2 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitRETURN(RETURN obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDALOAD(DALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitSIPUSH(SIPUSH obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDSUB(DSUB obj) {
        logger.debug(obj);
    }

    @Override
    public void visitL2F(L2F obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ICMPGT(IF_ICMPGT obj) {
        logger.debug(obj);
    }

    @Override
    public void visitF2D(F2D obj) {
        logger.debug(obj);
    }

    @Override
    public void visitI2L(I2L obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ACMPNE(IF_ACMPNE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitPOP(POP obj) {
        logger.debug(obj);
    }

    @Override
    public void visitI2S(I2S obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFEQ(IFEQ obj) {
        logger.debug(obj);
    }

    @Override
    public void visitSWAP(SWAP obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIOR(IOR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIREM(IREM obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIASTORE(IASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitNEWARRAY(NEWARRAY obj) {
        logger.debug(obj);
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitINEG(INEG obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLCMP(LCMP obj) {
        logger.debug(obj);
    }

    @Override
    public void visitJSR_W(JSR_W obj) {
        logger.debug(obj);
    }

    @Override
    public void visitMULTIANEWARRAY(MULTIANEWARRAY obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDUP_X2(DUP_X2 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitSALOAD(SALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFNONNULL(IFNONNULL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDMUL(DMUL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFNE(IFNE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ICMPLE(IF_ICMPLE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLDC2_W(LDC2_W obj) {
        logger.debug(obj);
    }

    @Override
    public void visitGETFIELD(GETFIELD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLADD(LADD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitNOP(NOP obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFALOAD(FALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitINSTANCEOF(INSTANCEOF obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFLE(IFLE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLXOR(LXOR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLRETURN(LRETURN obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFCONST(FCONST obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIUSHR(IUSHR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitBALOAD(BALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDUP2(DUP2 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ACMPEQ(IF_ACMPEQ obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIMPDEP1(IMPDEP1 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitMONITORENTER(MONITORENTER obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLSHL(LSHL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDCMPG(DCMPG obj) {
        logger.debug(obj);
    }

    @Override
    public void visitD2L(D2L obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIMPDEP2(IMPDEP2 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitL2D(L2D obj) {
        logger.debug(obj);
    }

    @Override
    public void visitRET(RET obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIFGT(IFGT obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIXOR(IXOR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFASTORE(FASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIRETURN(IRETURN obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ICMPNE(IF_ICMPNE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFLOAD(FLOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLDIV(LDIV obj) {
        logger.debug(obj);
    }

    @Override
    public void visitPUTSTATIC(PUTSTATIC obj) {
        logger.debug(obj);
    }

    @Override
    public void visitAALOAD(AALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitD2I(D2I obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIF_ICMPEQ(IF_ICMPEQ obj) {
        logger.debug(obj);
    }

    @Override
    public void visitAASTORE(AASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitARETURN(ARETURN obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDUP2_X1(DUP2_X1 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFNEG(FNEG obj) {
        logger.debug(obj);
    }

    @Override
    public void visitGOTO_W(GOTO_W obj) {
        logger.debug(obj);
    }

    @Override
    public void visitD2F(D2F obj) {
        logger.debug(obj);
    }

    @Override
    public void visitGOTO(GOTO obj) {
        logger.debug(obj);
    }

    @Override
    public void visitISUB(ISUB obj) {
        logger.debug(obj);
    }

    @Override
    public void visitF2I(F2I obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDNEG(DNEG obj) {
        logger.debug(obj);
    }

    @Override
    public void visitICONST(ICONST obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFDIV(FDIV obj) {
        logger.debug(obj);
    }

    @Override
    public void visitI2B(I2B obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLNEG(LNEG obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLREM(LREM obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIMUL(IMUL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitIADD(IADD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLSHR(LSHR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLOOKUPSWITCH(LOOKUPSWITCH obj) {
        logger.debug(obj);
    }

    @Override
    public void visitDUP_X1(DUP_X1 obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFCMPL(FCMPL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitI2C(I2C obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLMUL(LMUL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLUSHR(LUSHR obj) {
        logger.debug(obj);
    }

    @Override
    public void visitISHL(ISHL obj) {
        logger.debug(obj);
    }

    @Override
    public void visitLALOAD(LALOAD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitASTORE(ASTORE obj) {
        logger.debug(obj);
    }

    @Override
    public void visitANEWARRAY(ANEWARRAY obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFRETURN(FRETURN obj) {
        logger.debug(obj);
    }

    @Override
    public void visitFADD(FADD obj) {
        logger.debug(obj);
    }

    @Override
    public void visitBREAKPOINT(BREAKPOINT obj) {
        logger.debug(obj);
    }
}
