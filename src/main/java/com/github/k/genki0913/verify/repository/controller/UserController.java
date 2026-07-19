package com.github.k.genki0913.verify.repository.controller;

import com.github.k.genki0913.verify.common.validation.UniqueEmailValidator;
import com.github.k.genki0913.verify.domain.User;

import com.github.k.genki0913.verify.repository.service.UserRegistrationService;
import com.github.k.genki0913.verify.repository.service.UserUpdateService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.k.genki0913.verify.repository.constant.View;
import com.github.k.genki0913.verify.repository.form.UserRegistForm;
import com.github.k.genki0913.verify.repository.form.UserUpdateForm;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRegistrationService userRegistrationService;
    private final UserUpdateService userUpdateService;
    private final UniqueEmailValidator uniqueEmailValidator;
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository,
            UserUpdateService userUpdateService,
            UniqueEmailValidator uniqueEmailValidator,
            UserRegistrationService userRegistrationService) {
        this.userRepository = userRepository;
        this.userUpdateService = userUpdateService;
        this.uniqueEmailValidator = uniqueEmailValidator;
        this.userRegistrationService = userRegistrationService;
    }

    /**
     * Webデータバインダーを初期化します。
     * <p>
     * バリデーション処理に {@link UniqueEmailValidator} を追加登録し、
     * フォーム入力値に対するカスタムバリデーションを有効化します。
     * </p>
     * 
     * @param binder
     *                   Webデータバインダー
     */
    @InitBinder("userRegistForm")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(uniqueEmailValidator);
    }

    /**
     * ユーザー一覧画面を表示します。
     * <p>
     * リクエストパラメータ {@code keyword} が指定されている場合は、
     * 該当するユーザー名で絞り込み検索を行い、それ以外の場合は全件取得します。
     * </p>
     * * @param keyword 検索キーワード（名前の部分一致）。指定がない場合は {@code null}。
     * 
     * @param model
     *                  ビューにデータを渡すためのモデル。検索結果やキーワードが格納されます。
     * @return ユーザー一覧画面のテンプレート名 ("user/view")
     */
    @GetMapping
    public String view(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);

        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("users", userRepository.findByNameContaining(keyword));
        } else {
            model.addAttribute("users", userRepository.findAll());
        }

        return View.VIEW;
    }

    /**
     * ユーザー登録画面を表示します。
     * <p>
     * 画面入力用オブジェクト {@link UserRegistForm} を初期化してモデルに追加します。
     * </p>
     * 
     * @param model
     *                  ビューにデータを渡すためのモデル。フォームオブジェクトが格納されます。
     * @return ユーザー登録画面のテンプレート名 ("user/regist")
     */
    @GetMapping("/regist")
    public String showRegistForm(Model model) {
        model.addAttribute("userRegistForm", new UserRegistForm());
        return View.REGIST;
    }

    /**
     * ユーザー登録処理を実行します。
     * <p>
     * 入力値のバリデーションを行い、エラーがある場合は登録画面へ戻ります。
     * 正常終了した場合は、ユーザー一覧画面へリダイレクトします。
     * </p>
     * 
     * @param form
     *                          画面から入力されたユーザー登録情報。{@code @Validated} によりチェックされます。
     * @param bindingResult
     *                          バリデーション結果を保持するオブジェクト。エラー有無の判定に使用します。
     * @return 処理結果に応じたビュー名またはリダイレクト先
     */
    @PostMapping("/regist")
    public Object regist(@Validated UserRegistForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return View.REGIST;
        }

        User user = new User(form);
        this.userRegistrationService.register(user);

        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userUpdateService.findById(id);
        model.addAttribute("userUpdateForm", new UserUpdateForm(user));
        return View.EDIT;
    }
}
