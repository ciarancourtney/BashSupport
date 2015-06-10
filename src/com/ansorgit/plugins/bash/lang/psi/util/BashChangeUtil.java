/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashChangeUtil.java, Class: BashChangeUtil
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 16.04.2009
 * Time: 16:48:49
 *
 * @author Joachim Ansorg
 */
public class BashChangeUtil {
    private static final String TEMP_FILE_NAME = "__.sh";

    @NotNull
    private static PsiFile createFileFromText(@NotNull final Project project, @NotNull final String name, @NotNull final FileType fileType, @NotNull final String text) {
        return PsiFileFactory.getInstance(project).createFileFromText(name, fileType, text);
    }

    public static PsiFile createDummyBashFile(Project project, String text) {
        return createFileFromText(project, TEMP_FILE_NAME, BashFileType.BASH_FILE_TYPE, text);
    }

    public static PsiElement createSymbol(Project project, String name) {
        final PsiElement functionElement = createDummyBashFile(project, name + "() { x; }");
        return functionElement.getFirstChild().getFirstChild();
    }

    public static PsiElement createWord(Project project, String name) {
        return createDummyBashFile(project, name).getFirstChild().getFirstChild().getFirstChild();
    }

    public static PsiElement createString(Project project, String content) {
        return createDummyBashFile(project, "\"" + content + "\"").getFirstChild().getFirstChild().getFirstChild();
    }

    public static PsiElement createAssignmentWord(Project project, String name) {
        final PsiElement assignmentCommand = createDummyBashFile(project, name + "=a").getFirstChild();

        return assignmentCommand.getFirstChild().getFirstChild();
    }

    public static PsiElement createVariable(Project project, String name, boolean withBraces) {
        if (withBraces) {
            String text = "${" + name + "}";
            PsiElement command = createDummyBashFile(project, text).getFirstChild().getFirstChild();

            final PsiElement[] result = new PsiElement[1];

            BashPsiUtils.visitRecursively(command, new BashVisitor() {
                @Override
                public void visitVarUse(BashVar var) {
                    result[0] = var;
                }
            });

            return result[0];
        }

        String text = "$" + name;
        PsiElement command = createDummyBashFile(project, text).getFirstChild().getFirstChild();

        return command.getFirstChild().getFirstChild();
    }

    public static PsiElement createShebang(Project project, String command, boolean addNewline) {
        String text = "#!" + command + (addNewline ? "\n" : "");
        return createDummyBashFile(project, text).getFirstChild();
    }

    public static PsiElement createNewline(Project project) {
        String text = "\n";
        return createDummyBashFile(project, text).getFirstChild();
    }

    public static PsiComment createComment(Project project, String comment) {
        String text = "#" + comment + "\n";

        PsiFile file = createDummyBashFile(project, text);
        return PsiTreeUtil.getChildOfType(file, PsiComment.class);
    }
}
