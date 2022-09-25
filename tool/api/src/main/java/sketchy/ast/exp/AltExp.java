package sketchy.ast.exp;

import sketchy.ast.exp.iterator.Itr;
import sketchy.ast.exp.iterator.LitItr;
import sketchy.ast.visitor.Visitor;
import sketchy.driver.Driver;
import sketchy.util.UniqueList;

import java.util.LinkedList;
import java.util.List;

/**
 * Alternative expression.
 */
public class AltExp<T> extends Exp<T> {

    private UniqueList<Exp<T>> exps;

    private Exp<T> exp;

    private boolean emptyPruned;

    public AltExp(List<Exp<T>> exps) {
        this.exps = new UniqueList<>(exps);
        this.exp = null;
        this.emptyPruned = false;
    }

    @Override
    public void stepRand() {
        removeEmptyExps();
        // TODO: consider if we need uniform distribution of choices
        //  inside each expression rather than of expressions.
        exp = exps.pick(Driver.rand);
        exp.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        for (Exp<T> e : exps) {
            if (e.hasRandChoice()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void setItr() {
        removeEmptyExps();
        itr = new LitItr<>(exps) {

            private Itr currItr;

            @Override
            public void next() {
                super.next();
                if (currItr == null || !currItr.hasNext()) {
                    exp = iterator.next();
                    currItr = exp.itr();
                }
                currItr.next();
            }

            @Override
            public void reset() {
                super.reset();
                currItr = null;
                for (Exp<T> exp : exps) {
                    exp.itr().reset();
                }
            }

            @Override
            public boolean hasNext() {
                if (isReset()) {
                    return iterator.hasNext();
                }
                return iterator.hasNext() || currItr.hasNext();
            }
        };
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            exp.accept(v);
            v.endVisit(this);
        }
    }

    private void removeEmptyExps() {
        if (emptyPruned) {
            return;
        }
        List<Exp<T>> nonEmptyExps = new LinkedList<>();
        for (Exp<T> e : exps) {
            if (e.hasChoice()) {
                nonEmptyExps.add(e);
            }
        }
        if (nonEmptyExps.isEmpty()) {
            throw new RuntimeException("No expression can be used!");
        }
        exps = new UniqueList<>(nonEmptyExps);
        emptyPruned = true;
    }
}
