/* This file was generated by SableCC (http://www.sablecc.org/). 
 * Then modified.
 */

package ast.node;

import ast.visitor.*;

@SuppressWarnings("nls")
public final class NotExp extends IExp
{
    private IExp _exp_;

    public NotExp(int _line_, int _pos_, IExp _exp_)
    {
        super(_line_, _pos_);
        setExp(_exp_);

    }

    @Override
    public int getNumExpChildren() { return 1; }
   
    @Override
    public Object clone()
    {
        return new NotExp(
                this.getLine(),
                this.getPos(),
                cloneNode(this._exp_));
    }

    public void accept(Visitor v)
    {
        v.visitNotExp(this);
    }

    public IExp getExp()
    {
        return this._exp_;
    }

    public void setExp(IExp node)
    {
        if(this._exp_ != null)
        {
            this._exp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._exp_ = node;
    }

    @Override
    void removeChild(Node child)
    {
        // Remove child
        if(this._exp_ == child)
        {
            this._exp_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(Node oldChild, Node newChild)
    {
        // Replace child
        if(this._exp_ == oldChild)
        {
            setExp((IExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
