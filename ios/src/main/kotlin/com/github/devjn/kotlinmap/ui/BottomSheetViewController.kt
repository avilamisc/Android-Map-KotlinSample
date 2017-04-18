package com.github.devjn.kotlinmap.ui


/**
 * Created by @author Jahongir on 18-Apr-17
 * devjn@jn-arts.com
 * BottomSheetViewController
 */

import apple.uikit.UIButton
import apple.uikit.UIView
import apple.uikit.UIViewController
import org.moe.natj.general.Pointer
import org.moe.natj.objc.ann.Selector

class BottomSheetViewController protected constructor(peer: Pointer): UIViewController(peer) {
    // holdView can be UIImageView instead
    lateinit var holdView: UIView
    lateinit var left: UIButton
    lateinit var right: UIButton


    @Selector("init")
    override external fun init(): BottomSheetViewController

/*    val fullView: CGFloat = 100
    var partialView: CGFloat {
        return UIScreen.main.bounds.height - (left.frame.maxY + UIApplication.shared.statusBarFrame.height)
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        val gesture = UIPanGestureRecognizer.alloc().initWithTargetAction(this, selector(BottomSheetViewController.panGesture))
        view().addGestureRecognizer(gesture)

        roundViews()
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        prepareBackgroundView()
    }

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)

        UIView.animateWithDurationAnimations(0.6, {
            val frame = this.view().frame()
            val yComponent = this.partialView
            this.view().setFrame(CoreGraphics.CGRectMake(0.0, yComponent,frame.size().width(), frame.size().height()))
        })
    }

    override fun didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction fun rightButton(sender: Any) {
        print("clicked")
    }

    @IBAction fun close(sender: Any) {
        UIView.animateWithDurationAnimations(0.3, {
            val frame = this.view().frame()
            this.view().setFrame(CoreGraphics.CGRectMake(0.0, this.partialView, frame.size().width(), frame.size().height()))
        })
    }

    fun panGesture(recognizer: UIPanGestureRecognizer) {

        val translation = recognizer.translation(in: this.view())
        val velocity = recognizer.velocity(in: this.view())
        val y = this.view().frame.minY
                if ( y + translation.y >= fullView) && (y + translation.y <= partialView ) {
            this.view().frame = CGRect(x: 0, y: y + translation.y, width: view().frame.width, height: view().frame.height)
            recognizer.setTranslation(CGPoint.zero, in: this.view())
        }

        if recognizer.state == .ended {
            var duration =  velocity.y < 0 ? Double((y - fullView) / -velocity.y) : Double((partialView - y) / velocity.y )

            duration = duration > 1.3 ? 1 : duration

            UIView.animate(withDuration: duration, delay: 0.0, options: [.allowUserInteraction], animations: {
            if  velocity.y >= 0 {
                this.view().frame = CGRect(x: 0, y: this.partialView, width: this.view().frame.width, height: this.view().frame.height)
            } else {
                this.view().frame = CGRect(x: 0, y: this.fullView, width: this.view().frame.width, height: this.view().frame.height)
            }

        }, compvalion: nil)
        }
    }

    fun roundViews() {
        view().layer().setCornerRadius(5.0)
        holdView.layer().setCornerRadius(3.0)
        left.layer().setCornerRadius(10.0)
        right.layer().setCornerRadius(10.0)
        left.layer().setBorderColor(UIColor.alloc().initWithDisplayP3RedGreenBlueAlpha(0.0, 148.0/255.0, 247.0/255.0, 1.0).initWithCGColor())
        left.layer().setBorderWidth(1.0)
        view().setClipsToBounds(true)
    }

    fun prepareBackgroundView(){
        val blurEffect = UIBlurEffect.alloc().initWithCoder(dark)
        val visualEffect = UIVisualEffectView.alloc().initWithEffect(blurEffect)
        val bluredView = UIVisualEffectView.alloc().initWithEffect(blurEffect)
        bluredView.contentView().addSubview(visualEffect)

        visualEffect.setFrame(UIScreen.mainScreen().bounds())
        bluredView.setFrame(UIScreen.mainScreen().bounds())

        view().insertSubviewAtIndex(bluredView, 0)
    }*/

}