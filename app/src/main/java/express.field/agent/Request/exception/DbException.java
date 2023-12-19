package express.field.agent.Request.exception;

public class DbException extends Exception
{
    public DbException(String message)
    {
        super(message);
    }

    public DbException(String message, Throwable t)
    {
        super(message, t);
    }

    public DbException(Throwable t)
    {
        super(t);
    }
}
