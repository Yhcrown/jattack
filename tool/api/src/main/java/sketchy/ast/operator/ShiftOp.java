package sketchy.ast.operator;

import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;

public enum ShiftOp implements AriOrShiftOp {
    SHIFTL("<<"),
    SHIFTR(">>"),
    USHIFTR(">>>");

    private final String strRep;

    ShiftOp(String strRep) {
        this.strRep = strRep;
    }

    @Override
    public String asStr() {
        return strRep;
    }

    public Number apply(Number left, int right) {
        if (left instanceof Integer) {
            return apply(left.intValue(), right);
        } else if (left instanceof Long) {
            return apply(left.longValue(), right);
        } else {
            throw new RuntimeException("Unsupported type: " + left.getClass());
        }
    }

    private int apply(int left, int right) {
        switch (this) {
        case SHIFTL:
            return left << right;
        case SHIFTR:
            return left >> right;
        case USHIFTR:
            return left >>> right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private long apply(long left, int right) {
        switch (this) {
        case SHIFTL:
            return left << right;
        case SHIFTR:
            return left >> right;
        case USHIFTR:
            return left >>> right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    public Expr<IntSort> buildZ3Expr(Expr<IntSort> left, Expr<IntSort> right, Context ctx) {
        BitVecExpr leftInBV = ctx.mkInt2BV(32, left);
        BitVecExpr rightInBV = ctx.mkInt2BV(32, right);
        BitVecExpr res;
        switch (this) {
        case SHIFTL:
            res = ctx.mkBVSHL(leftInBV, rightInBV);
            break;
        case SHIFTR:
            res = ctx.mkBVASHR(leftInBV, rightInBV);
            break;
        case USHIFTR:
            res = ctx.mkBVLSHR(leftInBV, rightInBV);
            break;
        default:
            throw new RuntimeException("Unsupported operator " + this.asStr());
        }
        return ctx.mkBV2Int(res, true);
    }
}
