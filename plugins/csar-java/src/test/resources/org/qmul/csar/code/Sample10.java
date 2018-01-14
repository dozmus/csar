@ApiClass(author="Deniz Ozmus")
public @interface FileChange {
    @Metadata(author = "DO", since = 1.0)
    String author();
    @Deprecated
    String date() default "N/A";
}
